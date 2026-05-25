package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCancelledEvent;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PaymentEventHandler {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final PaymentService paymentService;

    //TODO: transfer the logic of this method to a separate service and call it from here.
    @ApplicationModuleListener()
    void onOrderCreated(OrderCreatedEvent event) throws StripeException {

        // Create a payment intent with stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(event.amount().longValueExact())
                .setCurrency("php")
                .build();

        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(event.orderId().toString()).build();

        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params, options);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setAmount(event.amount());
        payment.setPaymentIntentId(paymentIntent.getId());
        payment.setClientSecret(paymentIntent.getClientSecret());
        paymentRepository.save(payment);
    }

    @ApplicationModuleListener
    void onOrderCancelled(OrderCancelledEvent event) throws StripeException {
        paymentService.cancelPaymentByOrderId(event.orderId());
    }

}
