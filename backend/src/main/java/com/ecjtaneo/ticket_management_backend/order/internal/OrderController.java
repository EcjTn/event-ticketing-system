package com.ecjtaneo.ticket_management_backend.order.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;

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

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or @orderService.canCancelOrder(#orderId, principal.userId)")
    public MessageResponseDto cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
