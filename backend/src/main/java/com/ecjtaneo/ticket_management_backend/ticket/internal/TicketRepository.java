package com.ecjtaneo.ticket_management_backend.ticket.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
