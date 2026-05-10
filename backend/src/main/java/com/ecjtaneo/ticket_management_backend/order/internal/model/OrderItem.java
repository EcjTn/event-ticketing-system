package com.ecjtaneo.ticket_management_backend.order.internal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Data
@Entity
@Table(name = "order_item", indexes = {
        @Index(name = "idx_order_item_order_id", columnList = "order_id"),
        @Index(name = "idx_order_item_event_tier_id", columnList = "event_tier_id") }, uniqueConstraints = {
                @UniqueConstraint(name = "uq_order_item_order_tier", columnNames = { "order_id", "event_tier_id" }) })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "event_tier_id", nullable = false)
    private Long eventTierId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private TicketTier tier;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}