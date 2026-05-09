package com.ecjtaneo.ticket_management_backend.shared.events;

import java.math.BigDecimal;

public record OrderCreatedEvent(
		Long eventId,
		Long orderId,
		Long userId,
		BigDecimal amount) {

}
