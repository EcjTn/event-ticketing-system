package com.ecjtaneo.ticket_management_backend.shared;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
