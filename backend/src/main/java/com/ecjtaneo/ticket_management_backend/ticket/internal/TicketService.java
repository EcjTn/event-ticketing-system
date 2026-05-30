package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    //TODO: add ticket validation method to check if its valid for the event.

    public void createTickets(OrderConfirmedEvent event) {
        //TODO: add null check for the event and its properties before processing and also its items.
        //TODO: Use a JdbcTemplate BatchUpdate insert to create multiple tickets in one query for better performance.
        //TODO: implement the logic for creating tickets based on the order confirmation event
        //TODO: Maybe use random UUID for ticket code generation.
    }

}
