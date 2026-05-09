package com.ecjtaneo.ticket_management_backend.order.internal;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderInfoResponseDto createOrder(@RequestBody CreateOrderRequestDto request, @CurrentUserId Long userId) {
        return orderService.createOrder(request, userId);
    }
}
