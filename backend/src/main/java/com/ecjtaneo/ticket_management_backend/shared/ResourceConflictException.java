package com.ecjtaneo.ticket_management_backend.shared;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
