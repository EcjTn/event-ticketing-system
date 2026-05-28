package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderService orderService;

    void onPaymentSucceeded(PaymentSucceededEvent event) {
        orderService.confirmOrder(event.orderId());
    }

    void onPaymentFailed(PaymentFailedEvent event) {
        //TODO: call order service to mark order as CANCELLED by order id AND release reserved tickets
    }

}
