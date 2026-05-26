package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSucceededEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// I moved the payment processors to different class
// because calling a Transactional method in the same class from another method doesn't work because of how Spring proxies work.
// and it makes more sense anyway.
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessor {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final ApplicationEventPublisher eventPublisher;

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
