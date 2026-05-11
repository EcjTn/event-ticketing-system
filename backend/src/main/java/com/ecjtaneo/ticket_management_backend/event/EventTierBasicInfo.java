package com.ecjtaneo.ticket_management_backend.event;

import java.math.BigDecimal;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

public record EventTierBasicInfo(
        TicketTier tier,
        BigDecimal price,
        Integer quantity,
        Integer soldCount) {

}
