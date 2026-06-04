package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

import java.math.BigDecimal;

public record OrderItemInfoResponse(
        Long eventTierId,
        TicketTier tier,
        Integer quantity,
        BigDecimal unitPrice
) {
}
