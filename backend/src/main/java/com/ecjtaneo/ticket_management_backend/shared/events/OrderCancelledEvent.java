package com.ecjtaneo.ticket_management_backend.shared.events;

import java.util.Map;

public record OrderCancelledEvent(
		Long orderId,
		Map<Long, Integer> tierQuantities) {
}
