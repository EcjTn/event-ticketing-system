package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderConfirmedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketInfoResponse;
import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketValidationRequest;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper mapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository, mapper, jdbcTemplate);
    }

    @Test
    void validateAndUseTicket_Success() {
        TicketValidationRequest request = new TicketValidationRequest(1L, "code123");
        Ticket ticket = new Ticket();
        ticket.setUniqueCode("code123");
        ticket.setEventId(1L);
        ticket.setStatus(TicketStatus.VALID);

        when(ticketRepository.findByUniqueCodeForUpdate("code123")).thenReturn(Optional.of(ticket));

        MessageResponse response = ticketService.validateAndUseTicket(request);

        assertThat(response.message()).isEqualTo("Ticket validated successfully.");
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.USED);

        verify(ticketRepository).findByUniqueCodeForUpdate("code123");
        verify(ticketRepository).save(ticket);
    }

    @Test
    void validateAndUseTicket_NotFound_ThrowsException() {
        TicketValidationRequest request = new TicketValidationRequest(1L, "nonexistent");

        when(ticketRepository.findByUniqueCodeForUpdate("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.validateAndUseTicket(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ticket with code nonexistent not found.");

        verify(ticketRepository).findByUniqueCodeForUpdate("nonexistent");
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void validateAndUseTicket_EventMismatch_ThrowsException() {
        TicketValidationRequest request = new TicketValidationRequest(2L, "code123");
        Ticket ticket = new Ticket();
        ticket.setUniqueCode("code123");
        ticket.setEventId(1L); // mismatched event
        ticket.setStatus(TicketStatus.VALID);

        when(ticketRepository.findByUniqueCodeForUpdate("code123")).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketService.validateAndUseTicket(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Ticket is not valid for this event.");

        verify(ticketRepository).findByUniqueCodeForUpdate("code123");
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void validateAndUseTicket_AlreadyUsed_ThrowsException() {
        TicketValidationRequest request = new TicketValidationRequest(1L, "code123");
        Ticket ticket = new Ticket();
        ticket.setUniqueCode("code123");
        ticket.setEventId(1L);
        ticket.setStatus(TicketStatus.USED);

        when(ticketRepository.findByUniqueCodeForUpdate("code123")).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketService.validateAndUseTicket(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Ticket has already been used or cancelled.");

        verify(ticketRepository).findByUniqueCodeForUpdate("code123");
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void validateAndUseTicket_Cancelled_ThrowsException() {
        TicketValidationRequest request = new TicketValidationRequest(1L, "code123");
        Ticket ticket = new Ticket();
        ticket.setUniqueCode("code123");
        ticket.setEventId(1L);
        ticket.setStatus(TicketStatus.CANCELLED);

        when(ticketRepository.findByUniqueCodeForUpdate("code123")).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketService.validateAndUseTicket(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Ticket has already been used or cancelled.");

        verify(ticketRepository).findByUniqueCodeForUpdate("code123");
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void getTicketsForUser_Success() {
        Long userId = 1L;
        List<Ticket> tickets = List.of(new Ticket());
        List<TicketInfoResponse> expectedList = List.of(mock(TicketInfoResponse.class));

        when(ticketRepository.findTop10ByUserIdOrderByIdDesc(userId)).thenReturn(tickets);
        when(mapper.toTicketInfoResponse(tickets)).thenReturn(expectedList);

        List<TicketInfoResponse> result = ticketService.getTicketsForUser(userId);

        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    void getTicketsForUserWithLastSeenId_Success() {
        Long userId = 1L;
        Long lastSeenId = 5L;
        List<Ticket> tickets = List.of(new Ticket());
        List<TicketInfoResponse> expectedList = List.of(mock(TicketInfoResponse.class));

        when(ticketRepository.findTop10ByUserIdAndIdLessThanOrderByIdDesc(userId, lastSeenId)).thenReturn(tickets);
        when(mapper.toTicketInfoResponse(tickets)).thenReturn(expectedList);

        List<TicketInfoResponse> result = ticketService.getTicketsForUser(userId, lastSeenId);

        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    void createTicketsOnOrderConfirmed_CallsJdbcTemplate() {
        OrderConfirmedEvent.OrderItemBasicInfo itemInfo = new OrderConfirmedEvent.OrderItemBasicInfo(TicketTier.VIP, BigDecimal.TEN);
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, "Event Name", List.of(itemInfo));

        when(jdbcTemplate.batchUpdate(anyString(), anyCollection(), anyInt(), any(ParameterizedPreparedStatementSetter.class)))
                .thenReturn(new int[0][0]);

        ticketService.createTicketsOnOrderConfirmed(event);

        verify(jdbcTemplate).batchUpdate(anyString(), eq(event.items()), eq(1), any(ParameterizedPreparedStatementSetter.class));
    }
}
