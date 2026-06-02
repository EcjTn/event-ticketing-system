package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketValidationRequest;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JdbcTemplate jdbcTemplate;

    //separate logic..?
    public MessageResponse validateAndUseTicket(TicketValidationRequest ticketValidationRequest) {
        Ticket ticket = ticketRepository.findByUniqueCodeForUpdate(ticketValidationRequest.uniqueCode())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with code " + ticketValidationRequest.uniqueCode() + " not found."));

        if (!ticket.getEventId().equals(ticketValidationRequest.eventId())) throw new ValidationException("Ticket is not valid for this event.");
        if(ticket.getStatus() == TicketStatus.USED || ticket.getStatus() == TicketStatus.CANCELLED) throw new ValidationException("Ticket has already been used.");

        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);

        return new MessageResponse("Ticket validated successfully.");
    }

    public void createTicketsOnOrderConfirmed(OrderConfirmedEvent event) {
        // ?::ticket_tier is used to cast the string value to the enum type in PostgreSQL.
        String sql = """
                INSERT INTO tickets (order_id, user_id, event_id, tier, price_paid, unique_code)
                VALUES (?, ?, ?, ?::ticket_tier, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                sql,
                event.items(),
                event.items().size(),
                (ps, items) -> {
                    String ticketUniqueCode = "TX-" + UUID.randomUUID();

                    ps.setLong(1, event.orderId());
                    ps.setLong(2, event.userId());
                    ps.setLong(3, event.eventId());
                    ps.setString(4, items.tier().name());
                    ps.setBigDecimal(5, items.price());
                    ps.setString(6, ticketUniqueCode);
                }
        );


    }

}
