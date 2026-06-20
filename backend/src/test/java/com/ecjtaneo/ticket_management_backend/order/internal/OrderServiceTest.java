package com.ecjtaneo.ticket_management_backend.order.internal;

import com.ecjtaneo.ticket_management_backend.event.EventApi;
import com.ecjtaneo.ticket_management_backend.event.EventBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.AdjustSoldCountData;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.*;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderItemRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCancelledEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.OrdersBatchExpiredEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OrderMapper mapper;

    @Mock
    private EventApi eventApi;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, orderItemRepository, eventPublisher, mapper, eventApi);
    }

    @Test
    void createOrder_Success() {
        Long userId = 10L;
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(2L, 3)));
        EventBasicInfo eventInfo = new EventBasicInfo(1L, "Event Name");
        EventTierBasicInfo tierInfo = new EventTierBasicInfo(TicketTier.VIP, BigDecimal.valueOf(100), 10, 5);

        when(eventApi.getPublishedEventInfo(1L)).thenReturn(eventInfo);
        when(eventApi.getLockEventTierForUpdate(2L)).thenReturn(tierInfo);

        OrderInfoResponse expectedResponse = mock(OrderInfoResponse.class);
        when(mapper.toOrderInfoResponseDto(any(Order.class))).thenReturn(expectedResponse);

        OrderInfoResponse result = orderService.createOrder(request, userId);

        assertThat(result).isEqualTo(expectedResponse);

        verify(eventApi).getPublishedEventInfo(1L);
        verify(eventApi).getLockEventTierForUpdate(2L);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(eventApi).batchIncrementEventTierSoldCount(anyList());
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_TooManyTickets_ThrowsException() {
        Long userId = 10L;
        // maxOrderItemsPerOrder is 5
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(2L, 6)));
        EventBasicInfo eventInfo = new EventBasicInfo(1L, "Event Name");

        when(eventApi.getPublishedEventInfo(1L)).thenReturn(eventInfo);

        assertThatThrownBy(() -> orderService.createOrder(request, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Too many tickets requested");

        verify(eventApi).getPublishedEventInfo(1L);
        verifyNoMoreInteractions(eventApi);
        verifyNoInteractions(orderRepository, orderItemRepository, eventPublisher);
    }

    @Test
    void createOrder_NotEnoughTickets_ThrowsException() {
        Long userId = 10L;
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(2L, 4)));
        EventBasicInfo eventInfo = new EventBasicInfo(1L, "Event Name");
        EventTierBasicInfo tierInfo = new EventTierBasicInfo(TicketTier.VIP, BigDecimal.valueOf(100), 10, 8); // only 2 available

        when(eventApi.getPublishedEventInfo(1L)).thenReturn(eventInfo);
        when(eventApi.getLockEventTierForUpdate(2L)).thenReturn(tierInfo);

        assertThatThrownBy(() -> orderService.createOrder(request, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not enough tickets available for tier VIP");

        verify(eventApi).getPublishedEventInfo(1L);
        verify(eventApi).getLockEventTierForUpdate(2L);
        verifyNoInteractions(orderRepository, orderItemRepository, eventPublisher);
    }

    @Test
    void ownsOrder_ReturnsValue() {
        when(orderRepository.existsByIdAndUserId(1L, 2L)).thenReturn(true);
        assertThat(orderService.ownsOrder(1L, 2L)).isTrue();
    }

    @Test
    void cancelOrder_Success() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setEventTierId(2L);
        item.setQuantity(3);
        order.setItems(List.of(item));

        when(orderRepository.findWithItemsForUpdateByIdAndStatus(orderId, OrderStatus.PENDING))
                .thenReturn(Optional.of(order));

        MessageResponse response = orderService.cancelOrder(orderId);

        assertThat(response.message()).isEqualTo("Order cancelled successfully");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        verify(eventApi).batchDecrementEventTierSoldCount(anyList());
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    void cancelOrder_NotFoundOrNotPending_ThrowsException() {
        Long orderId = 1L;
        when(orderRepository.findWithItemsForUpdateByIdAndStatus(orderId, OrderStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Order not found or already cancelled");

        verifyNoInteractions(eventApi, eventPublisher);
    }

    @Test
    void cancelOrderOnPaymentFailure_Success() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setEventTierId(2L);
        item.setQuantity(3);
        order.setItems(List.of(item));

        when(orderRepository.findWithItemsForUpdateByIdAndStatus(orderId, OrderStatus.PENDING))
                .thenReturn(Optional.of(order));

        orderService.cancelOrderOnPaymentFailure(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(eventApi).batchDecrementEventTierSoldCount(anyList());
    }

    @Test
    void confirmOrderOnPaymentSucceeded_Success() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(2L);
        order.setEventId(3L);
        order.setEventName("Event Name");
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setTier(TicketTier.VIP);
        item.setUnitPrice(BigDecimal.TEN);
        order.setItems(List.of(item));

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));

        orderService.confirmOrderOnPaymentSucceeded(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(eventPublisher).publishEvent(any(OrderConfirmedEvent.class));
    }

    @Test
    void confirmOrderOnPaymentSucceeded_NotFound_ThrowsException() {
        Long orderId = 1L;
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.confirmOrderOnPaymentSucceeded(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void confirmOrderOnPaymentSucceeded_NotPending_ThrowsException() {
        Long orderId = 1L;
        Order order = new Order();
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrderOnPaymentSucceeded(orderId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only pending orders can be confirmed");

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void getPendingOrdersForUser_Success() {
        Long userId = 1L;
        List<Order> orders = List.of(new Order());
        List<OrderBasicInfoResponse> expectedList = List.of(mock(OrderBasicInfoResponse.class));

        when(orderRepository.findTop10ByUserIdAndStatusOrderByIdDesc(userId, OrderStatus.PENDING)).thenReturn(orders);
        when(mapper.toOrderBasicInfoResponseDtoList(orders)).thenReturn(expectedList);

        List<OrderBasicInfoResponse> result = orderService.getPendingOrdersForUser(userId);

        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    void getPendingOrdersForUserWithLastSeenId_Success() {
        Long userId = 1L;
        Long lastSeenId = 5L;
        List<Order> orders = List.of(new Order());
        List<OrderBasicInfoResponse> expectedList = List.of(mock(OrderBasicInfoResponse.class));

        when(orderRepository.findTop10ByUserIdAndStatusAndIdLessThanOrderByIdDesc(userId, OrderStatus.PENDING, lastSeenId))
                .thenReturn(orders);
        when(mapper.toOrderBasicInfoResponseDtoList(orders)).thenReturn(expectedList);

        List<OrderBasicInfoResponse> result = orderService.getPendingOrdersForUser(userId, lastSeenId);

        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    void getPendingOrderDetailsForUser_Success() {
        Long orderId = 1L;
        Long userId = 2L;
        Order order = new Order();
        OrderFullInfoResponse expectedResponse = mock(OrderFullInfoResponse.class);

        when(orderRepository.findWithItemsByIdAndUserIdAndStatus(orderId, userId, OrderStatus.PENDING))
                .thenReturn(Optional.of(order));
        when(mapper.toOrderFullInfoResponseDto(order)).thenReturn(expectedResponse);

        OrderFullInfoResponse result = orderService.getPendingOrderDetailsForUser(orderId, userId);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getPendingOrderDetailsForUser_NotFound_ThrowsException() {
        Long orderId = 1L;
        Long userId = 2L;

        when(orderRepository.findWithItemsByIdAndUserIdAndStatus(orderId, userId, OrderStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getPendingOrderDetailsForUser(orderId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void processExpiredOrders_Success() {
        List<Long> expiredIds = List.of(1L, 2L);
        when(orderRepository.batchCancelExpiredOrdersAndReturnIds()).thenReturn(expiredIds);

        EventTierQuantityAggregateProjection projection = mock(EventTierQuantityAggregateProjection.class);
        when(projection.eventTierId()).thenReturn(3L);
        when(projection.totalQuantity()).thenReturn(5);

        when(orderItemRepository.aggregateTiersByOrderIds(expiredIds)).thenReturn(List.of(projection));

        orderService.processExpiredOrders();

        verify(eventApi).batchDecrementEventTierSoldCount(anyList());
        verify(eventPublisher).publishEvent(any(OrdersBatchExpiredEvent.class));
    }

    @Test
    void processExpiredOrders_NoExpiredOrders_ReturnsEarly() {
        when(orderRepository.batchCancelExpiredOrdersAndReturnIds()).thenReturn(Collections.emptyList());

        orderService.processExpiredOrders();

        verifyNoInteractions(orderItemRepository, eventApi, eventPublisher);
    }
}
