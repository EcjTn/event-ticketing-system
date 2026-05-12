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
    List<Order> findTop50ByStatusAndExpiresAtBefore(OrderStatus status, LocalDateTime expiresAt);

    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id IN :ids AND o.status = :previousStatus")
    void updateStatusByIds(@Param("newStatus") OrderStatus newStatus, @Param("ids") List<Long> ids,
            @Param("previousStatus") OrderStatus previousStatus);

    // Trying out new method for batch cancelling

    @Query(value = """
            WITH expired e AS (
                SELECT id FROM orders
                WHERE expires_at < NOW()
                AND status = 'PENDING'
                LIMIT 500
                FOR UPDATE SKIP LOCKED
            )
            UPDATE orders o
            SET status = 'CANCELLED'
            FROM expired e
            WHERE o.id = e.id
            RETURNING o.id;
            """, nativeQuery = true)
    List<Long> cancelExpiredOrders();

}
