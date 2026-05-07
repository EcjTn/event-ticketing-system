package com.ecjtaneo.ticket_management_backend.event.internal.dto;

import com.ecjtaneo.ticket_management_backend.event.internal.model.EventStatus;
import java.time.LocalDateTime;
import java.util.List;

public record EventInfoResponseDto(
    Long id,
    String name,
    LocalDateTime date,
    String venue,
    String description,
    String imageUrl,
    EventStatus status,
    LocalDateTime createdAt,
    List<EventTierInfoResponseDto> tiers,
    Integer availableTickets
) {
    
}
