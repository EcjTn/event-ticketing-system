package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketInfoResponse;
import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketValidationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService service;

    @GetMapping("/me")
    List<TicketInfoResponse> getUserTickets(@RequestParam(required = false) Long lastSeenId, @CurrentUserId Long userId) {
        if (lastSeenId == null) return service.getTicketsForUser(userId);
        else return service.getTicketsForUser(userId, lastSeenId);
    }

    @PostMapping("/validate")
    MessageResponse validateTicket(@RequestBody @Valid TicketValidationRequest dto) {
        return service.validateAndUseTicket(dto);
    }

}
