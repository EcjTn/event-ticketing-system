package com.ecjtaneo.ticket_management_backend.event.internal.dto;

import com.ecjtaneo.ticket_management_backend.event.internal.model.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// changed from record to dto
// record is immutable, can not set the calculated total available tickets after initialized.
@Data
public class EventInfoResponseDto{
    Long id;
    String name;
    LocalDateTime date;
    String venue;
    String description;
    String imageUrl;
    EventStatus status;
    LocalDateTime createdAt;
    List<EventTierInfoResponseDto> tiers;
    Integer availableTickets;
    
}
