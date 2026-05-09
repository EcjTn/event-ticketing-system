package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findWithItemsById(Long id);

    boolean existsByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findByIdAndStatus(Long id, OrderStatus status);
}
