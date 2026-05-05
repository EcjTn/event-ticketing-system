package com.ecjtaneo.ticket_management_backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
