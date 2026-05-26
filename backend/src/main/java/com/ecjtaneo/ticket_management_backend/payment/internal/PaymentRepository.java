package com.ecjtaneo.ticket_management_backend.payment.internal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Optional<Payment> findByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId")
    public Optional<Payment> findByOrderIdForUpdate(@Param("orderId") Long orderId);

    @Query(value = """
        UPDATE payment
        SET status = 'CANCELLED'
        WHERE order_id IN (:orderIds)
        AND status = 'PENDING'
        RETURNING payment_intent_id
        """, nativeQuery = true)
    List<String> bulkCancelAndReturnIntentIds(@Param("orderIds") List<Long> orderIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.paymentIntentId = :paymentIntentId")
    Optional<Payment> findByPaymentIntentIdForUpdate(@Param("paymentIntentId") String paymentIntentId);

}
