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
        //TODO: Decide how to create multiple tickets in one order event. Maybe we need to include the order items in the event to know how many tickets to create.
        //do a for each loop on on the order items?
        //TODO: implement the logic for creating tickets based on the order confirmation event
        //TODO: Maybe use random UUID for ticket code generation.
        Ticket ticket = new Ticket();
        ticket.setOrderId(event.orderId());
        ticket.setUserId(event.userId());
        ticket.setEventId(event.eventId());
        ticket.setPricePaid(event.totalAmount()); //refactor this. use the order item's price instead of the total amount for the order.
    }

}
