package com.ecjtaneo.ticket_management_backend.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.event.model.EventTier;

public interface EventTierRepository extends JpaRepository<EventTier, Long> {
}
