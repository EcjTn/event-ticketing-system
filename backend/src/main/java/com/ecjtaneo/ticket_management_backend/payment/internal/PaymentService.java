package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.order.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PaymentService {
    private final PaymentRepository paymentRepository;

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
