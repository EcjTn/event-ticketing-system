package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService service;

//    @
//    MessageResponse validateAndUseTicket(String uniqueCode, Long eventId) {
//        return service.validateAndUseTicket(uniqueCode, eventId);
//    }

}
