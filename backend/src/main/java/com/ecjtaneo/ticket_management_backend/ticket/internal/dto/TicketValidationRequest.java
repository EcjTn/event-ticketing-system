package com.ecjtaneo.ticket_management_backend.ticket.internal.dto;

public record TicketValidationRequest(Long eventId, String uniqueCode) {
}
