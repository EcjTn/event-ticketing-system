package com.ecjtaneo.ticket_management_backend.event.internal.dto;

public record EventBasicInfoResponseDto(
    Long id,
    String name,
    String imageUrl
) {
    
}
