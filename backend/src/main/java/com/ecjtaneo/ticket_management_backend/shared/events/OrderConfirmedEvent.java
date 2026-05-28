package com.ecjtaneo.ticket_management_backend.shared.events;

import java.math.BigDecimal;

public record OrderConfirmedEvent(Long orderId, Long userId, Long eventId, BigDecimal totalAmount) {
}
