package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderBasicInfoResponse;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderFullInfoResponse;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequest;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {
    private final OrderService orderService;

    // Stopping at PENDING orders for now. Add more endpoints for COMPLETED/CANCELLED orders if needed.
    @GetMapping("/pending")
    List<OrderBasicInfoResponse> getPendingOrders(@CurrentUserId Long userId, @RequestParam(name = "cursor", required = false) Long lastSeenId) {
        if(lastSeenId == null)
            return orderService.getPendingOrdersForUser(userId);
        return orderService.getPendingOrdersForUser(userId, lastSeenId);
    }

    @GetMapping("/pending/{orderId}")
    OrderFullInfoResponse getPendingOrderDetails(@PathVariable Long orderId, @CurrentUserId Long userId) {
        return orderService.getPendingOrderDetailsForUser(orderId, userId);
    }


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
