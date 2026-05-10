package com.ecjtaneo.ticket_management_backend.event.internal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

import jakarta.persistence.LockModeType;

public interface EventTierRepository extends JpaRepository<EventTier, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventTier e WHERE e.id = :id AND (e.quantity > e.soldCount)")
    public Optional<EventTier> findByIdAndAvailable(@Param("id") Long id);

    @Modifying
    @Query("UPDATE EventTier e SET e.soldCount = e.soldCount + :quantity WHERE e.id = :id")
    public void incrementSoldCount(@Param("id") Long id, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE EventTier e SET e.soldCount = e.soldCount - :quantity WHERE e.id = :id")
    public void decrementSoldCount(@Param("id") Long id, @Param("quantity") int quantity);
}
