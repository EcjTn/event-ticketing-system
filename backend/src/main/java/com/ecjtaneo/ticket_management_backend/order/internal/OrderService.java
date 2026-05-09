package com.ecjtaneo.ticket_management_backend.order.internal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.event.EventApi;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.CreateOrderRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.order.internal.dto.OrderItemRequestDto;
import com.ecjtaneo.ticket_management_backend.order.internal.mapper.OrderMapper;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderItemRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.repository.OrderRepository;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper mapper;
    private final EventApi eventApi;
    private final LocalDateTime orderExpiresAt = LocalDateTime.now().plusMinutes(15);

    // TODO: emit application event after order creation
    // TODO: Separate order creation logic for readability
    @Transactional
    public OrderInfoResponseDto createOrder(CreateOrderRequestDto request, Long userId) {
        eventApi.validateEventIsPublished(request.eventId());
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDto item : request.items()) {

            // Locks the event tier on this call
            EventTierBasicInfo tier = eventApi.getEventTierInfo(item.eventTierId());

            if(tier.quantity() < item.quantity()) {
                throw new ValidationException("Not enough tickets available");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(null);
            orderItem.setEventTierId(item.eventTierId());
            orderItem.setTier(tier.tier());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(tier.price());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                tier.price().multiply(
                    BigDecimal.valueOf(item.quantity())
                ));

        }

        Order order = new Order(); // reminder default status = PENDING 
        order.setUserId(userId);
        order.setEventId(request.eventId());
        order.setTotalAmount(totalAmount);
        order.setExpiresAt(orderExpiresAt);
        orderRepository.save(order);

        orderItems.forEach(item -> item.setOrder(order));
        orderItemRepository.saveAll(orderItems);

    
        orderItems.forEach(item -> eventApi.incrementEventTierSoldCount(item.getEventTierId(), item.getQuantity()));

        return mapper.toOrderInfoResponseDto(order);
    }

}
