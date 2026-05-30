package com.ecjtaneo.ticket_management_backend.event.internal.dto;

public record EventBasicInfoResponse(
    Long id,
    String name,
    String imageUrl,
    String venue,
    String description
) {
    
}
