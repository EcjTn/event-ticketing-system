package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;

public record PaymentResponseDto(String clientSecret, PaymentStatus status) {
}
