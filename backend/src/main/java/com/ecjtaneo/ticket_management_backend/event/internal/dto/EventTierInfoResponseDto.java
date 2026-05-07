package com.ecjtaneo.ticket_management_backend.event.internal.dto;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import java.math.BigDecimal;

public record EventTierInfoResponseDto(
    Long id,
    TicketTier tier,
    BigDecimal price,
    Integer quantity
) {
    
}
