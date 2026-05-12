package com.ecjtaneo.ticket_management_backend.shared.events;

import com.ecjtaneo.ticket_management_backend.shared.TicketRestoreView;

public record EventTierStockRestoreEvent(TicketRestoreView restoreView) {
}
