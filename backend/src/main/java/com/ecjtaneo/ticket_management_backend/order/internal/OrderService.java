package com.ecjtaneo.ticket_management_backend.order.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponse;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCancelledEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.event.EventApi;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.AdjustSoldCountData;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrdersBatchExpiredEvent;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequest;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderItemRequest;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderItemRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final static long expirationCheckRateMs = 600_000; // 10 minutes
    private final static int maxOrderItemsPerOrder = 5;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper mapper;
    private final EventApi eventApi;

    @Transactional
    OrderInfoResponse createOrder(CreateOrderRequest request, Long userId) {
        eventApi.validateEventIsPublished(request.eventId());

        List<OrderItem> orderItems = processOrderItems(request.items());
        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        Order order = buildAndSaveOrder(request.eventId(), userId, totalAmount);
        saveOrderItems(orderItems, order);
        updateEventTierCounts(orderItems);

        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), userId, totalAmount, order.getExpiresAt()));

        return mapper.toOrderInfoResponseDto(order);
    }

    private List<OrderItem> processOrderItems(List<OrderItemRequest> itemRequests) {
        // sort by tier id to prevent deadlocks when locking tiers
        // If 2 threads
        // T1 (Order 1): lock tier 1 then tier 2
        // T2 (Order 2): lock tier 2 then tier 1
        // causes deadlock, so we sort by tier id to ensure consistent locking order
        List<OrderItemRequest> sorted = itemRequests.stream()
                .sorted(Comparator.comparing(OrderItemRequest::eventTierId))
                .toList();

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest item : sorted) {

            if (item.quantity() > maxOrderItemsPerOrder) {
                throw new ValidationException("Too many tickets requested");
            }

            // Locks the event tier on this call
            EventTierBasicInfo tier = eventApi.lockEventTierForUpdate(item.eventTierId());
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
        List<AdjustSoldCountData> adjustments = orderItems.stream()
                .map(item -> new AdjustSoldCountData(item.getEventTierId(), item.getQuantity()))
                .toList();
        eventApi.batchIncrementEventTierSoldCount(adjustments);
    }

    boolean ownsOrder(Long orderId, Long userId) {
        return orderRepository.existsByIdAndUserId(orderId, userId);
    }

    // TODO: move the cancellation logic to a separate method and call it from both
    // cancelOrder and cancelOrderOnPaymentFailure to avoid code duplication
    @Transactional
    MessageResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findWithItemsForUpdateByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new ValidationException("Order not found or already cancelled"));

        order.setStatus(OrderStatus.CANCELLED);

        List<AdjustSoldCountData> adjustments = order.getItems().stream()
                .map(item -> new AdjustSoldCountData(item.getEventTierId(), item.getQuantity()))
                .toList();
        eventApi.batchDecrementEventTierSoldCount(adjustments);

        // publish OrderCancelledEvent so Payment module can cancel PaymentIntent on
        // Stripe
        // keep decoupled from Payment module by just publishing an event and let the
        // Payment module handle the rest
        // unlike eventApi, orders should not know about the existence of the Payment
        // module, so we use eventPublisher directly to publish an event and let the
        // Payment module handle it if it exists
        eventPublisher.publishEvent(new OrderCancelledEvent(order.getId()));

        return new MessageResponse("Order cancelled successfully");
    }

    @Transactional
    void cancelOrderOnPaymentFailure(Long orderId) {
        Order order = orderRepository.findWithItemsForUpdateByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new ValidationException("Order not found or already cancelled"));

        order.setStatus(OrderStatus.CANCELLED);

        List<AdjustSoldCountData> adjustments = order.getItems().stream()
                .map(item -> new AdjustSoldCountData(item.getEventTierId(), item.getQuantity()))
                .toList();
        eventApi.batchDecrementEventTierSoldCount(adjustments);
    }

    @Transactional
    void confirmOrder(Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ValidationException("Only pending orders can be confirmed");
        }

        order.setStatus(OrderStatus.CONFIRMED);

        // publish OrderConfirmedEvent so Tickets are created -- ticket module listens
        // to this event to create tickets
        eventPublisher.publishEvent(new OrderConfirmedEvent(
                order.getId(), order.getUserId(), order.getEventId(), order.getTotalAmount()));
    }

    @Scheduled(fixedDelay = expirationCheckRateMs)
    @Transactional
    void processExpiredOrders() {
        log.info("Starting to process expired orders");

        // Retrieve expired order IDs using the new batch cancellation query
        List<Long> orderIds = orderRepository.batchCancelExpiredOrdersAndReturnIds();

        if (orderIds.isEmpty()) {
            log.info("No expired orders found");
            return;
        }

        // Retrieve stock restore projections using the aggregate tiers query
        List<EventTierQuantityAggregateProjection> restoreViews = orderItemRepository
                .aggregateTiersByOrderIds(orderIds);

        if (!restoreViews.isEmpty()) {
            List<AdjustSoldCountData> adjustments = restoreViews.stream()
                    .map(aggregate -> new AdjustSoldCountData(aggregate.eventTierId(), aggregate.totalQuantity()))
                    .toList();
            eventApi.batchDecrementEventTierSoldCount(adjustments);
        }

        // Fire OrdersBatchExpiredEvent after stock is restored
        eventPublisher.publishEvent(new OrdersBatchExpiredEvent(orderIds));
    }

}
