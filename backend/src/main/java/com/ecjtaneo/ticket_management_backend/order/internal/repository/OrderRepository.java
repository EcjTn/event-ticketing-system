package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecjtaneo.ticket_management_backend.order.internal.dto.EventTierQuantityAggregate;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findByIdAndStatus(Long id, OrderStatus status);

    // Trying out new method for batch cancelling
    @Query(value = """
            WITH cancelled AS (
                UPDATE orders o
                SET status = :newStatus
                WHERE o.id IN (
                    SELECT id
                    FROM orders
                    WHERE status = :prevStatus
                    AND expires_at < now()
                    ORDER BY expires_at
                    LIMIT 100
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING o.id
            )
            SELECT
                oi.event_tier_id AS eventTierId,
                SUM(oi.quantity) AS totalQuantity
            FROM order_items oi
            JOIN cancelled c ON c.id = oi.order_id
            GROUP BY oi.event_tier_id;
            """, nativeQuery = true)
    List<EventTierQuantityAggregate> cancelExpiredOrdersBatch(@Param("prevStatus") OrderStatus prevStatus,
            @Param("newStatus") OrderStatus newStatus);

}
