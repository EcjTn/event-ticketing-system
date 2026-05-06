package com.ecjtaneo.ticket_management_backend.event.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
