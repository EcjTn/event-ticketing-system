package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequest;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;

import lombok.RequiredArgsConstructor;

//TODO: Add endpoint for users to view their orders(all kind of orders)

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {
    private final OrderService orderService;

    @PostMapping
    OrderInfoResponse createOrder(@RequestBody CreateOrderRequest request, @CurrentUserId Long userId) {
        return orderService.createOrder(request, userId);
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or @orderService.ownsOrder(#orderId, principal.userId)")
    MessageResponse cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
