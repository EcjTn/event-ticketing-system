package com.ecjtaneo.ticket_management_backend.event.internal.dto;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateEventTierDto(
    @NotNull(message = "Tier is required")
    TicketTier tier,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}