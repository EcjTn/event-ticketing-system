package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @ApplicationModuleListener()
    public void onOrderCreated(OrderCreatedEvent event) {
        Payment payment = new Payment();
        payment.setEventId(event.eventId());
        payment.setOrderId(event.orderId());
        payment.setUserId(event.userId());
        payment.setAmount(event.amount());
        paymentRepository.save(payment);
    }

}
