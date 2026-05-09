package com.ecjtaneo.ticket_management_backend.event.internal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String name;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false, length = 255)
    private String venue;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventTier> tiers;
}
