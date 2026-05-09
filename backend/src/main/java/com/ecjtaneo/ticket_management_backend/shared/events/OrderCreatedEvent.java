package com.ecjtaneo.ticket_management_backend.shared.events;

import java.math.BigDecimal;

public record OrderCreatedEvent(
		Long orderId,
		Long userId,
		BigDecimal amount) {

}
