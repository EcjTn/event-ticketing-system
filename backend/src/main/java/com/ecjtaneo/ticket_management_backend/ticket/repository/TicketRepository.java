package com.ecjtaneo.ticket_management_backend.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.ticket.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
