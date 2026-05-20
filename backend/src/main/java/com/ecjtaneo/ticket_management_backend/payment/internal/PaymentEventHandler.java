package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.stripe.StripeClient;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.order.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PaymentEventHandler {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;

    //TODO: Columns to add in Payment entity: paymentIntentId, clientSecret, currency
    //TODO: Create payment intent with Stripe API and save the payment info to the database
    @ApplicationModuleListener()
    void onOrderCreated(OrderCreatedEvent event) {
        Payment payment = new Payment();
        payment.setEventId(event.eventId());
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setAmount(event.amount());
        paymentRepository.save(payment);
    }

}
