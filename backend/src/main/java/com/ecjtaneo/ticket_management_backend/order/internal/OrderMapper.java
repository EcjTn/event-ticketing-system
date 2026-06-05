package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderBasicInfoResponse;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderFullInfoResponse;
import org.mapstruct.Mapper;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponse;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderInfoResponse toOrderInfoResponseDto(Order order);
    OrderFullInfoResponse toOrderFullInfoResponseDto(Order order);
    List<OrderBasicInfoResponse> toOrderBasicInfoResponseDtoList(List<Order> orders);
}
