package com.ecjtaneo.ticket_management_backend.order.internal.dto;

import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderBasicInfoResponse(
        Long orderId,
        String eventName,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
){

}
