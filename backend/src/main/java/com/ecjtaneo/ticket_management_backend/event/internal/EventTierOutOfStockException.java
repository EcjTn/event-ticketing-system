package com.ecjtaneo.ticket_management_backend.event.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class EventTierOutOfStockException extends ErrorResponseException {
    public EventTierOutOfStockException(String message) {
        super(HttpStatus.CONFLICT);
        this.setTitle("Event Tier Out of Stock");
        this.setDetail(message);
    }
}
