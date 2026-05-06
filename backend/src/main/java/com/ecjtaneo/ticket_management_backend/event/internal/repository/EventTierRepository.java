package com.ecjtaneo.ticket_management_backend.event.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

public interface EventTierRepository extends JpaRepository<EventTier, Long> {
}
