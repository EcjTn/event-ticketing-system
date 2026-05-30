package com.ecjtaneo.ticket_management_backend.payment.internal.dto;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;

public record PaymentResponse(String clientSecret, PaymentStatus status) {
}
