package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderService orderService;

    @ApplicationModuleListener
    void onPaymentSucceeded(PaymentSucceededEvent event) {
        if(event == null) return;
        orderService.confirmOrderOnPaymentSucceeded(event.orderId());
    }

    @ApplicationModuleListener
    void onPaymentFailed(PaymentFailedEvent event) {
        if(event == null) return;
        orderService.cancelOrderOnPaymentFailure(event.orderId());
    }

}
