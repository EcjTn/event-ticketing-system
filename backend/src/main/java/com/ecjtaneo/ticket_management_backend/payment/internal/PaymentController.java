package com.ecjtaneo.ticket_management_backend.payment.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    PaymentResponseDto getPaymentInfoByOrderId(@PathVariable Long orderId) {
        return paymentService.getPaymentInfoByOrderId(orderId);
    }

}
