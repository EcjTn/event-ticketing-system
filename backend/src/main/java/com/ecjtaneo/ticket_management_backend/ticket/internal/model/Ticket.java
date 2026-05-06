package com.ecjtaneo.ticket_management_backend.ticket.internal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ticket", indexes = {
    @Index(name = "idx_ticket_user_id", columnList = "user_id"),
    @Index(name = "idx_ticket_event_id", columnList = "event_id")
})
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private TicketTier tier;
    
    @Column(name = "price_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;
    
    @Column(name = "unique_code", nullable = false, unique = true, length = 255)
    private String uniqueCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.VALID;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
}

