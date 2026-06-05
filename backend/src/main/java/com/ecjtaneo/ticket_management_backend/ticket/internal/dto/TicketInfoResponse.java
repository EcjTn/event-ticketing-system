package com.ecjtaneo.ticket_management_backend.ticket.internal.dto;

import com.ecjtaneo.ticket_management_backend.ticket.internal.model.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketInfoResponse(
        Long id,
        Long orderId,
        Long eventId,
        String eventName,
        String tier,
        TicketStatus status,
        BigDecimal pricePaid,
        String uniqueCode,
        LocalDateTime createdAt
) {
}
