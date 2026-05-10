package com.ecjtaneo.ticket_management_backend;

import org.junit.jupiter.api.Test;

import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;

import static org.assertj.core.api.Assertions.assertThat;

public class TicketTest {
    @Test
    void ticketTierCountMatchesEnumSize() {
        assertThat(TicketTier.COUNT).isEqualTo(TicketTier.values().length);
    }
}
