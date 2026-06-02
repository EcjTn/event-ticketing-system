package com.ecjtaneo.ticket_management_backend.ticket.internal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.uniqueCode = :uniqueCode")
    public Optional<Ticket> findByUniqueCodeForUpdate(String uniqueCode);

}
