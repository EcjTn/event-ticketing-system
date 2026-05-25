package com.ecjtaneo.ticket_management_backend.payment.internal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Optional<Payment> findByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId")
    public Optional<Payment> findByOrderIdForUpdate(@Param("orderId") Long orderId);

}
