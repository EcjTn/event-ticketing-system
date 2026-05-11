package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findByIdAndStatus(Long id, OrderStatus status);

    @EntityGraph(attributePaths = { "items" })
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.expiresAt < :dateTime LIMIT 50")
    List<Order> findExpiredOrders(@Param("status") OrderStatus status, @Param("dateTime") LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id IN :ids AND o.status = 'PENDING' ")
    void updateStatusByIds(@Param("status") OrderStatus status, @Param("ids") List<Long> ids);
    
}
