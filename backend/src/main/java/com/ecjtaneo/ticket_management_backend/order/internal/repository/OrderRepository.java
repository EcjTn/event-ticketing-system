package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findWithItemsById(Long id);
}
