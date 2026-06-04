package com.ecjtaneo.ticket_management_backend.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ResourceNotFoundException extends ErrorResponseException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Resource Not Found");
        this.setDetail(message);
    }
}
