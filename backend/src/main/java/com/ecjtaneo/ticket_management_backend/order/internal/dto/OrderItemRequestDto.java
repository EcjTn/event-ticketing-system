package com.ecjtaneo.ticket_management_backend.order.internal.dto;

public record OrderItemRequestDto(
    Long eventTierId,
    Integer quantity
) {
    
}
