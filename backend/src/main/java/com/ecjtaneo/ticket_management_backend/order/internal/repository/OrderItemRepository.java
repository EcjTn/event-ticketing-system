package com.ecjtaneo.ticket_management_backend.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecjtaneo.ticket_management_backend.order.internal.model.OrderItem;
import com.ecjtaneo.ticket_management_backend.order.internal.EventTierQuantityAggregateProjection;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Query 2: aggregate tiers by order IDs
    @Query(value = """
            SELECT
                oi.event_tier_id AS eventTierId,
                SUM(oi.quantity) AS totalQuantity
            FROM order_item oi
            WHERE oi.order_id IN (:orderIds)
            GROUP BY oi.event_tier_id
            """, nativeQuery = true)
    List<EventTierQuantityAggregateProjection> aggregateTiersByOrderIds(
            @Param("orderIds") List<Long> orderIds);

}
