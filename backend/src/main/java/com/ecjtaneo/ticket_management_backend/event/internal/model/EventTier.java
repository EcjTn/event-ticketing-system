package com.ecjtaneo.ticket_management_backend.event.internal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "event_tier", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "tier"}))
public class EventTier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private TicketTier tier;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "sold_count", nullable = false)
    private Integer soldCount = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}