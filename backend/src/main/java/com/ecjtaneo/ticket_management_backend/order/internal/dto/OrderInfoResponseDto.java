package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

public record OrderInfoResponseDto(
        Long id,
        BigDecimal totalAmount,
        OrderStatus status,
        Long eventId,
        LocalDateTime expiresAt) {

}
