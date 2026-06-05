package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderCancelledEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrdersBatchExpiredEvent;
import com.stripe.exception.StripeException;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PaymentEventHandler {

    private final PaymentService paymentService;

    @ApplicationModuleListener
    void onOrderCreated(OrderCreatedEvent event) throws StripeException {
        if(event == null) return;
        paymentService.createPaymentOnOrderCreated(event);
    }

    @ApplicationModuleListener
    void onOrderCancelled(OrderCancelledEvent event) throws StripeException {
        if(event == null) return;
        paymentService.cancelPaymentByOrderIdOnOrderCancelled(event.orderId());
    }

    @ApplicationModuleListener
    void onOrdersBatchExpired(OrdersBatchExpiredEvent event) throws StripeException {
        if(event == null) return;
        paymentService.cancelPaymentsOnOrdersBatchExpired(event.orderIds());
    }

}
