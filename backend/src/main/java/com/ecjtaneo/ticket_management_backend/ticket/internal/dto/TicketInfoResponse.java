package com.ecjtaneo.ticket_management_backend.ticket.internal.dto;

import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketInfoResponse(
        Long id,
        Long orderId,
        Long eventId,
        String eventName,
        EventTier tier,
        TicketStatus status,
        BigDecimal pricePaid,
        String uniqueCode,
        LocalDateTime createdAt
) {
}
