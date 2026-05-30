package com.ecjtaneo.ticket_management_backend.order.internal.dto;

public record OrderItemRequest(
    Long eventTierId,
    Integer quantity
) {
    
}
