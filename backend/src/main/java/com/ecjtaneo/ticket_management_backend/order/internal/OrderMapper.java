package com.ecjtaneo.ticket_management_backend.order.internal;

import org.mapstruct.Mapper;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponse;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderInfoResponse toOrderInfoResponseDto(Order order);
}
