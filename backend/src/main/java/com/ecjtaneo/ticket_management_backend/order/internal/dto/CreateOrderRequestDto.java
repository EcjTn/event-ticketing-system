package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import java.util.List;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequestDto(
        @NotNull Long eventId,
        @Valid @Size(max = TicketTier.COUNT, message = "Too many ticket tiers") List<OrderItemRequestDto> items) {
}
