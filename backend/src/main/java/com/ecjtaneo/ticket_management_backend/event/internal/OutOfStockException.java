package com.ecjtaneo.ticket_management_backend.event.internal;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
