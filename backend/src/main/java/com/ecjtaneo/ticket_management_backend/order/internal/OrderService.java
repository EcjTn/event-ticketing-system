package com.ecjtaneo.ticket_management_backend.order.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.event.EventApi;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderItemRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.mapper.OrderMapper;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderItemRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCancelledEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final long EXPIRATION_CHECK_RATE_MS = 600_000; // 10 minutes
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper mapper;
    private final EventApi eventApi;

    @Transactional
    public OrderInfoResponseDto createOrder(CreateOrderRequestDto request, Long userId) {
        eventApi.validateEventIsPublished(request.eventId());

        List<OrderItem> orderItems = processOrderItems(request.items());
        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        Order order = buildAndSaveOrder(request.eventId(), userId, totalAmount);
        saveOrderItems(orderItems, order);
        updateEventTierCounts(orderItems);

        eventPublisher.publishEvent(new OrderCreatedEvent(request.eventId(), order.getId(), userId, totalAmount));

        return mapper.toOrderInfoResponseDto(order);
    }

    private List<OrderItem> processOrderItems(List<OrderItemRequestDto> itemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDto item : itemRequests) {
            // Locks the event tier on this call
            EventTierBasicInfo tier = eventApi.getEventTierInfo(item.eventTierId());

            if (tier.quantity() < item.quantity()) {
                throw new ValidationException("Not enough tickets available");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setEventTierId(item.eventTierId());
            orderItem.setTier(tier.tier());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(tier.price());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order buildAndSaveOrder(Long eventId, Long userId, BigDecimal totalAmount) {
        Order order = new Order(); // reminder default status = PENDING
        order.setUserId(userId);
        order.setEventId(eventId);
        order.setTotalAmount(totalAmount);
        order.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        return orderRepository.save(order);
    }

    private void saveOrderItems(List<OrderItem> orderItems, Order order) {
        orderItems.forEach(item -> item.setOrder(order));
        orderItemRepository.saveAll(orderItems);
    }

    private void updateEventTierCounts(List<OrderItem> orderItems) {
        orderItems.forEach(item -> eventApi.incrementEventTierSoldCount(item.getEventTierId(), item.getQuantity()));
    }

    public boolean canCancelOrder(Long orderId, Long userId) {
        return orderRepository.existsByIdAndUserId(orderId, userId);
    }

    @Transactional
    public MessageResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new ValidationException("Order not found or already cancelled"));

        order.setStatus(OrderStatus.CANCELLED);

        publishOrderCancelledEvent(order);

        return new MessageResponseDto("Order cancelled successfully");
    }

    private void publishOrderCancelledEvent(Order order) {
        // key = tier id
        // value = quantity
        // currently max tiers per order is 3
        Map<Long, Integer> tierQuantities = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getEventTierId, OrderItem::getQuantity));

        eventPublisher.publishEvent(new OrderCancelledEvent(order.getId(), tierQuantities));
    }

    @Scheduled(fixedDelay = EXPIRATION_CHECK_RATE_MS)
    @Transactional
    public void processExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findTop50ByStatusAndExpiresAtBefore(
                OrderStatus.PENDING, LocalDateTime.now());

        if (expiredOrders.isEmpty())
            return;

        List<Long> expiredOrderIds = expiredOrders.stream()
                .map(Order::getId)
                .toList();

        orderRepository.updateStatusByIds(OrderStatus.CANCELLED, expiredOrderIds, OrderStatus.PENDING);

        for (Order order : expiredOrders) {
            publishOrderCancelledEvent(order);
        }
    }

}
