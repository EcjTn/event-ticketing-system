package com.ecjtaneo.ticket_management_backend.event.internal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

import jakarta.persistence.LockModeType;

public interface EventTierRepository extends JpaRepository<EventTier, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventTier e WHERE e.id = :id AND (e.quantity > e.soldCount)")
    public Optional<EventTier> findByIdAndAvailable(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventTier e WHERE e.id = :id")
    public Optional<EventTier> findByIdForUpdate(@Param("id") Long id);
}
