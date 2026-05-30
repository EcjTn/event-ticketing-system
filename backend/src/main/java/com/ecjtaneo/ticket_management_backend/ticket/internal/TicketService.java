package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final JdbcTemplate jdbcTemplate;

    //TODO: add ticket validation method to check if its valid for the event.

    public void createTickets(OrderConfirmedEvent event) {
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
