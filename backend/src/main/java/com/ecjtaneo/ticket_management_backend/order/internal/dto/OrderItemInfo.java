package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

public record OrderItemInfo(
        Long eventTierId,
        TicketTier tier,
        Integer quantity,
        String unitPrice
) {
}
