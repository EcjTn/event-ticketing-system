package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSuccessfulEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @ApplicationModuleListener()
    public void processOrderCreation(OrderCreatedEvent event) {
        // TODO: add payment entity record with repository
        // Just for demo: simulate a random payment success/failure
        boolean shouldFail = false;

        if (shouldFail) {
            eventPublisher.publishEvent(new PaymentFailedEvent(event.orderId()));
        } else {
            eventPublisher.publishEvent(new PaymentSuccessfulEvent(event.orderId()));
        }

    }

}
