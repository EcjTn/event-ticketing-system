package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderFullInfoResponse(
        Long orderId,
        Long eventId,
        String eventName,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<OrderItemInfoResponse> items
) {
}
