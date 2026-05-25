package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;

import java.util.Optional;

interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Optional<Payment> findByOrderId(Long orderId);

}
