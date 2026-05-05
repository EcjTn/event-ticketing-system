package com.ecjtaneo.ticket_management_backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
