package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.payment.internal.dto.PaymentResponseDto;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSucceededEvent;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final String STRIPE_WEBHOOK_PAYMENT_SUCCEEDED = "payment_intent.succeeded";
    private static final String STRIPE_WEBHOOK_PAYMENT_FAILED = "payment_intent.payment_failed";
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    // Stripe's payment intents are cancelled on-demand when the user tries to access an expired payment when fetching payment info on frontend.
    //TODO: use specific exceptions instead of generic ones and handle them in the module's controller advice.
    PaymentResponseDto getPaymentInfoByOrderIdAndValid(Long orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if(payment.getStatus() != PaymentStatus.PENDING) throw new ValidationException("Only pending payments can be processed");
        if(payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            stripeClient.v1().paymentIntents().cancel(payment.getPaymentIntentId());
            payment.setStatus(PaymentStatus.CANCELLED);

            throw new ValidationException("Payment has expired and is now cancelled");
        }

        return new PaymentResponseDto(payment.getClientSecret(), payment.getStatus());
    }

    // Event handler for OrderCancelledEvent should call this method to cancel the payment intent.
    @Transactional
    public void cancelPaymentByOrderId(Long orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if(payment.getStatus() != PaymentStatus.PENDING) throw new ValidationException("Only pending payments can be cancelled");

        stripeClient.v1().paymentIntents().cancel(payment.getPaymentIntentId());
        payment.setStatus(PaymentStatus.CANCELLED);
    }

    //Used by payment event handler for OrderCreatedEvents
    @Transactional
    public void createPayment(OrderCreatedEvent event) throws StripeException {
        // Create a payment intent with stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(event.amount().longValueExact())
                .setCurrency("php")
                .build();

        //Idempotency key in case of duplicate OrderCreatedEvents.
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(event.orderId().toString()).build();

        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params, options);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setAmount(event.amount());
        payment.setExpiresAt(event.expiresAt());
        payment.setPaymentIntentId(paymentIntent.getId());
        payment.setClientSecret(paymentIntent.getClientSecret());
        paymentRepository.save(payment);
    }

    @Transactional
    public void cancelPayments(List<Long> orderIds) throws StripeException {
        if (orderIds == null || orderIds.isEmpty()) return;

        log.info("Bulk cancelling payments for order IDs: {}", orderIds);

        List<String> intentIds = paymentRepository.bulkCancelAndReturnIntentIds(orderIds);

        if(intentIds.isEmpty()) {
            log.info("No payments found to cancel for order IDs: {}", orderIds);
            return;
        }

        for (String intentId : intentIds) {
            log.info("Cancelling Stripe PaymentIntent: {}", intentId);
            stripeClient.v1().paymentIntents().cancel(intentId);
        }
    }

    public void handleWebhook(String payload, String sigHeader) throws StripeException {
        //Verify the webhook signature + Deserialize the raw json
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        log.info("Received Stripe webhook event: {}", event.getType());

        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ValidationException("Failed to deserialize PaymentIntent"));

        switch (event.getType()) {
            case STRIPE_WEBHOOK_PAYMENT_SUCCEEDED -> processPaymentSuccess(paymentIntent.getId());
            case STRIPE_WEBHOOK_PAYMENT_FAILED -> processPaymentFailure(paymentIntent.getId());
            default -> log.info("Unhandled Stripe event: {}", event.getType());
        }
    }

    //TODO: Move processPaymentSuccess() and processPaymentFailure(), both are transactional methods
    @Transactional
    public void processPaymentSuccess(String paymentIntentId) throws StripeException {
        Payment payment = paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for payment intent ID: " + paymentIntentId));

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            log.info("Race condition detected: Payment for order {} was already CANCELLED in DB. Issuing immediate Stripe refund.", payment.getOrderId());

            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            stripeClient.v1().refunds().create(refundParams);
        }
        else if (payment.getStatus() == PaymentStatus.PENDING) {
            log.info("Processing successful payment for order {}. Updating status to SUCCESS.", payment.getOrderId());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            // Publish PaymentSucceededEvent so tickets can be created and Orders can be marked as CONFIRMED in Order module
            eventPublisher.publishEvent(new PaymentSucceededEvent(payment.getOrderId()));
        }
        else {
            log.info("Payment for order {} is already in status {}. Skipping.", payment.getOrderId(), payment.getStatus());
        }

    }

    @Transactional
    public void processPaymentFailure(String paymentIntentId) throws StripeException {
        Payment payment = paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for payment intent ID: " + paymentIntentId));

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            log.info("Payment is already CANCELLED in DB for order {}. No action needed.", payment.getOrderId());
        }
        else if(payment.getStatus() == PaymentStatus.PENDING) {
            log.info("Processing failed payment for order {}. Updating status to FAILED.", payment.getOrderId());
            payment.setStatus(PaymentStatus.FAILED);

            // Publish PaymentFailedEvent so Order module can mark the order as CANCELLED and release reserved tickets
            // Order module should react to this event
            eventPublisher.publishEvent(new PaymentFailedEvent(payment.getOrderId()));
        }
        else {
            log.info("Payment for order {} is already in status {}. Skipping.", payment.getOrderId(), payment.getStatus());
        }


    }
}
