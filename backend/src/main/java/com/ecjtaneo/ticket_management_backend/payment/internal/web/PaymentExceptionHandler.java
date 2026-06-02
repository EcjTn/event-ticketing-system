package com.ecjtaneo.ticket_management_backend.payment.internal.web;

import com.ecjtaneo.ticket_management_backend.payment.internal.PaymentExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PaymentExceptionHandler {

    @ExceptionHandler(PaymentExpiredException.class)
    ProblemDetail handleExpiredPayment(PaymentExpiredException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Payment expired");
        pd.setDetail(ex.getMessage());
        return pd;
    }

}
