package com.ecjtaneo.ticket_management_backend.payment.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;

interface PaymentRepository extends JpaRepository<Payment, Long> {
}
