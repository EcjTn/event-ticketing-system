package com.ecjtaneo.ticket_management_backend.shared;

// note: this is still a test for the new method of Scheduled Task (Order cancellation)
// and to restore ticket count

// TODO: Move this file to correct location after testing
// TODO: Maybe use more formal naming

public record TicketRestoreView(Long eventTierId, Integer totalQuantity) {

}
