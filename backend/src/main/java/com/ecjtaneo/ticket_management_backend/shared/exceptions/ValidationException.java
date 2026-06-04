package com.ecjtaneo.ticket_management_backend.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ValidationException extends ErrorResponseException {
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST);
        this.setTitle("Validation Error");
        this.setDetail(message);
    }
}
