package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class PaymentExpiredException extends ErrorResponseException {
    public PaymentExpiredException(String message) {
        super(HttpStatus.GONE);
        this.setTitle("Payment Expired");
        this.setDetail(message);
    }
}
