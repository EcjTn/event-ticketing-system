package com.ecjtaneo.ticket_management_backend.user.internal.dto;

import java.time.LocalDateTime;

public record UserInfoResponseDto(
    Long id,
    String name,
    String role,
    String profileImageUrl,
    LocalDateTime createdAt
) {
    
}
