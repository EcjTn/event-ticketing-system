package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.ecjtaneo.ticket_management_backend.order.internal.EventTierQuantityAggregateProjection;
import com.ecjtaneo.ticket_management_backend.order.internal.model.Order;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderStatus;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findWithItemsByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = { "items" })
    Optional<Order> findWithItemsForUpdateByIdAndStatus(Long id, OrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(Long id);

    List<Order> findTop10ByUserIdAndStatusOrderByIdDesc(Long userId, OrderStatus status);
    List<Order> findTop10ByUserIdAndStatusAndIdLessThanOrderByIdDesc(Long userId, OrderStatus status, Long lastSeenId);

    // Trying out new method for batch cancelling -- now not used, switched to v2 below
//    @Query(value = """
//            WITH cancelled AS (
//                UPDATE orders o
//                SET status = 'CANCELLED'
//                WHERE o.id IN (
//                    SELECT id
//                    FROM orders
//                    WHERE status = 'PENDING'
//                    AND expires_at < now()
//                    ORDER BY expires_at
//                    LIMIT 100
//                    FOR UPDATE SKIP LOCKED
//                )
//                RETURNING o.id
//            )
//            SELECT
//                oi.event_tier_id AS eventTierId,
//                SUM(oi.quantity) AS totalQuantity
//            FROM order_item oi
//            JOIN cancelled c ON c.id = oi.order_id
//            GROUP BY oi.event_tier_id;
//            """, nativeQuery = true)
//    List<EventTierQuantityAggregateProjection> batchCancelExpiredOrdersAndAggregateTier();



    // v2 of the order cancelling batch
    // this version splits the query into two query, 1 for batch cancelling
    // returning only the order ids,
    // and another query for aggregating the tier quantities based on the cancelled (transferred in OrderItemRepository)
    // order ids.
    // I decided to split when Payment module is involved since we need to cancel
    // the payment intents in a separate transaction, which requires me to get the
    // cancelled order ids first before cancelling the payment intents.
    @Query(value = """
            UPDATE orders
            SET status = 'CANCELLED'
            WHERE id IN (
                SELECT id
                FROM orders
                WHERE status = 'PENDING'
                AND expires_at < now()
                ORDER BY expires_at
                LIMIT 100
                FOR UPDATE SKIP LOCKED
            )
            RETURNING id
            """, nativeQuery = true)
    List<Long> batchCancelExpiredOrdersAndReturnIds();

}
