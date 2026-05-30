package com.ecjtaneo.ticket_management_backend.event.internal.dto;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
        @NotBlank(message = "Event name is required") @Size(max = 255, message = "Event name must not exceed 255 characters") String name,

        @NotNull(message = "Event date is required") LocalDateTime date,

        @NotBlank(message = "Venue is required") @Size(max = 255, message = "Venue must not exceed 255 characters") String venue,

        String description,

        @NotNull(message = "Event tiers are required") @Size(min = 1, max = TicketTier.COUNT, message = "At least one event tier is required and max three") @Valid List<CreateEventTierRequest> tiers) {
}