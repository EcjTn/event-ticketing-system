package com.ecjtaneo.ticket_management_backend.event.internal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {
    public List<Event> findTop10ByStatusOrderByIdDesc(EventStatus status);
    public List<Event> findTop10ByIdLessThanOrderByIdDesc(Long lastSeenId);
}
