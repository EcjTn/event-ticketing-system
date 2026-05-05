package com.ecjtaneo.ticket_management_backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
