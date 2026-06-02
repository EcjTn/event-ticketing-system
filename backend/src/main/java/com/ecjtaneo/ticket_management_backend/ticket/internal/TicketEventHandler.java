package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketEventHandler {
    private final TicketService ticketService;

    @ApplicationModuleListener
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        ticketService.createTicketsOnOrderConfirmed(event);
    }

}
