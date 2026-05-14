package com.ecjtaneo.ticket_management_backend.order.internal;

import org.mapstruct.Mapper;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderInfoResponseDto toOrderInfoResponseDto(Order order);
}
