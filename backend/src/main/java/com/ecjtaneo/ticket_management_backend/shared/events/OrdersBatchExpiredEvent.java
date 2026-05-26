package com.ecjtaneo.ticket_management_backend.shared.events;

import java.util.List;

public record OrdersBatchExpiredEvent(List<Long> orderIds) {
}
