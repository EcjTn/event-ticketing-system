package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import java.util.List;

import jakarta.validation.Valid;

public record CreateOrderRequestDto(
    Long eventId,
    @Valid
    List<OrderItemRequestDto> items
) {
    
}
