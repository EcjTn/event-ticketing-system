package com.ecjtaneo.ticket_management_backend.order.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.event.EventApi;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.EventTierQuantityAdjustment;
import com.ecjtaneo.ticket_management_backend.order.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.EventTierQuantityAggregate;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderItemRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.mapper.OrderMapper;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderItemRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final long expirationCheckRateMs = 600_000; // 10 minutes
    private final int maxOrderItemsPerOrder = 5;
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
        // sort by tier id to prevent deadlocks when locking tiers
        // If 2 threads
        // T1 (Order 1): lock tier 1 then tier 2
        // T2 (Order 2): lock tier 2 then tier 1
        // causes deadlock, so we sort by tier id to ensure consistent locking order
        List<OrderItemRequestDto> sorted = itemRequests.stream()
                .sorted(Comparator.comparing(OrderItemRequestDto::eventTierId))
                .toList();

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto item : sorted) {

            if (item.quantity() > maxOrderItemsPerOrder) {
                throw new ValidationException("Too many tickets requested");
            }

            // Locks the event tier on this call
            EventTierBasicInfo tier = eventApi.getEventTierInfo(item.eventTierId());
            Integer available = tier.quantity() - tier.soldCount();

            if (available < item.quantity()) {
                throw new ValidationException("Not enough tickets available for tier " + tier.tier());
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
        List<EventTierQuantityAdjustment> adjustments = orderItems.stream()
                .map(item -> new EventTierQuantityAdjustment(item.getEventTierId(), item.getQuantity()))
                .toList();
        eventApi.batchIncrementEventTierSoldCount(adjustments);
    }

    public boolean canCancelOrder(Long orderId, Long userId) {
        return orderRepository.existsByIdAndUserId(orderId, userId);
    }

    // TODO: Re implement cancelOrder method(single order not batch)

    @Scheduled(fixedDelay = expirationCheckRateMs)
    @Transactional
    public void processExpiredOrders() {
        log.info("Starting to process expired orders");

        // cancel 500 expired orders and get the list of event tiers
        // and quantities to restore
        List<EventTierQuantityAggregate> restoreViews = orderRepository.cancelExpiredOrdersBatch(
                OrderStatus.PENDING, OrderStatus.CANCELLED);

        if (restoreViews.isEmpty()) {
            log.info("No expired orders found");
            return;
        }

        List<EventTierQuantityAdjustment> adjustments = restoreViews.stream()
                .map(aggregate -> new EventTierQuantityAdjustment(aggregate.eventTierId(), aggregate.totalQuantity()))
                .toList();

        eventApi.batchDecrementEventTierSoldCount(adjustments);
    }

}
