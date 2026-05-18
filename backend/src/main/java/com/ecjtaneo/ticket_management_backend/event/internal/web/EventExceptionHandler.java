package com.ecjtaneo.ticket_management_backend.event.internal.web;

import com.ecjtaneo.ticket_management_backend.event.internal.OutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class EventExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    ProblemDetail handleOutOfStock(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Out of stock");
        pd.setDetail(ex.getMessage());
        return pd;
    }

}
