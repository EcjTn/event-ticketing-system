package com.ecjtaneo.ticket_management_backend.payment.internal;

public class PaymentExpiredException extends RuntimeException {
    public PaymentExpiredException(String message) {
        super(message);
    }
}
