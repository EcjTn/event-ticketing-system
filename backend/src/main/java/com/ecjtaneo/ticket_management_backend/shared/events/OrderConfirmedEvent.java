package com.ecjtaneo.ticket_management_backend.shared.events;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmedEvent(Long orderId, Long userId, Long eventId, List<OrderBasicInfo> items) {

    public record OrderBasicInfo(
        TicketTier tier,
        BigDecimal price
    ) {}

}
