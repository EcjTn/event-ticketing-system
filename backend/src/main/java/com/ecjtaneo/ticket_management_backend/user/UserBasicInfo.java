package com.ecjtaneo.ticket_management_backend.user;

// Mostly used outside the module. (ex: Spring security UserDetails)
public record UserBasicInfo(
    Long id,
    String name,
    String role,
    String password
) {
    
}
