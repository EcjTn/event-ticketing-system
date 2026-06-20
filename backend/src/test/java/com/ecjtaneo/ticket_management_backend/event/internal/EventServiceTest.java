package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.AdjustSoldCountData;
import com.ecjtaneo.ticket_management_backend.event.EventBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.*;
import com.ecjtaneo.ticket_management_backend.event.internal.model.*;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventTierRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.enums.TicketTier;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.ecjtaneo.ticket_management_backend.storage.StorageApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTierRepository eventTierRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private EventMapper mapper;

    @Mock
    private StorageApi storageApi;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, eventTierRepository, jdbcTemplate, mapper, storageApi);
    }

    @Test
    void getEvents_Success() {
        Event event = new Event();
        List<Event> events = List.of(event);
        List<EventBasicInfoResponse> expectedList = List.of(mock(EventBasicInfoResponse.class));

        when(eventRepository.findTop10ByStatusOrderByIdDesc(EventStatus.PUBLISHED)).thenReturn(events);
        when(mapper.toEventBasicInfoDtoList(events)).thenReturn(expectedList);

        List<EventBasicInfoResponse> result = eventService.getEvents();

        assertThat(result).isEqualTo(expectedList);
        verify(eventRepository).findTop10ByStatusOrderByIdDesc(EventStatus.PUBLISHED);
        verify(mapper).toEventBasicInfoDtoList(events);
    }

    @Test
    void getEventsWithLastSeenId_Success() {
        Long lastSeenId = 5L;
        Event event = new Event();
        List<Event> events = List.of(event);
        List<EventBasicInfoResponse> expectedList = List.of(mock(EventBasicInfoResponse.class));

        when(eventRepository.findTop10ByIdLessThanAndStatusOrderByIdDesc(lastSeenId, EventStatus.PUBLISHED)).thenReturn(events);
        when(mapper.toEventBasicInfoDtoList(events)).thenReturn(expectedList);

        List<EventBasicInfoResponse> result = eventService.getEvents(lastSeenId);

        assertThat(result).isEqualTo(expectedList);
        verify(eventRepository).findTop10ByIdLessThanAndStatusOrderByIdDesc(lastSeenId, EventStatus.PUBLISHED);
        verify(mapper).toEventBasicInfoDtoList(events);
    }

    @Test
    void getEventInfoById_Success() {
        Long id = 1L;
        Event event = new Event();
        event.setId(id);

        EventTier tier1 = new EventTier();
        tier1.setQuantity(100);
        tier1.setSoldCount(20);

        EventTier tier2 = new EventTier();
        tier2.setQuantity(50);
        tier2.setSoldCount(10);

        event.setTiers(List.of(tier1, tier2));

        EventInfoResponse expectedResponse = new EventInfoResponse();
        expectedResponse.setId(id);

        when(eventRepository.findWithTiersById(id)).thenReturn(Optional.of(event));
        when(mapper.toEventInfoDto(event)).thenReturn(expectedResponse);

        EventInfoResponse result = eventService.getEventInfoById(id);

        assertThat(result.getAvailableTickets()).isEqualTo(120); // (100-20) + (50-10) = 120
        verify(eventRepository).findWithTiersById(id);
        verify(mapper).toEventInfoDto(event);
    }

    @Test
    void getEventInfoById_NotFound_ThrowsException() {
        Long id = 1L;
        when(eventRepository.findWithTiersById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventInfoById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: " + id);

        verify(eventRepository).findWithTiersById(id);
        verifyNoInteractions(mapper);
    }

    @Test
    void updateEventStatus_Success() {
        Long id = 1L;
        Event event = new Event();
        event.setId(id);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        MessageResponse response = eventService.updateEventStatus(id, EventStatus.PUBLISHED);

        assertThat(response.message()).isEqualTo("Event status updated to PUBLISHED");
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(eventRepository).findById(id);
        verify(eventRepository).save(event);
    }

    @Test
    void updateEventStatus_NotFound_ThrowsException() {
        Long id = 1L;
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEventStatus(id, EventStatus.PUBLISHED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found with id: " + id);

        verify(eventRepository).findById(id);
        verifyNoMoreInteractions(eventRepository);
    }

    @Test
    void createEvent_Success() throws IOException {
        CreateEventRequest dto = new CreateEventRequest("Event Name", LocalDateTime.now(), "Venue", "Description",
                List.of(new CreateEventTierRequest(TicketTier.VIP, BigDecimal.TEN, 100, "VIP description")));
        MultipartFile image = mock(MultipartFile.class);
        Long createdBy = 123L;

        Event event = new Event();
        event.setId(1L);

        EventTier eventTier = new EventTier();

        when(mapper.toEvent(dto)).thenReturn(event);
        when(storageApi.uploadEventPhoto(image, event.getId())).thenReturn("http://image.url");
        when(eventRepository.save(event)).thenReturn(event);
        when(mapper.toEventTier(any(CreateEventTierRequest.class))).thenReturn(eventTier);

        MessageResponse response = eventService.createEvent(dto, image, createdBy);

        assertThat(response.message()).isEqualTo("Event created successfully");
        assertThat(event.getCreatedBy()).isEqualTo(createdBy);
        assertThat(event.getImageUrl()).isEqualTo("http://image.url");

        verify(mapper).toEvent(dto);
        verify(storageApi).uploadEventPhoto(image, event.getId());
        verify(eventRepository).save(event);
        verify(mapper).toEventTier(any(CreateEventTierRequest.class));
        verify(eventTierRepository).saveAll(anyList());
    }

    @Test
    void getPublishedEventInfo_Success() {
        Long id = 1L;
        Event event = new Event();
        event.setId(id);
        event.setName("Published Event");
        event.setStatus(EventStatus.PUBLISHED);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        EventBasicInfo result = eventService.getPublishedEventInfo(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Published Event");
        verify(eventRepository).findById(id);
    }

    @Test
    void getPublishedEventInfo_NotFound_ThrowsException() {
        Long id = 1L;
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getPublishedEventInfo(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found");

        verify(eventRepository).findById(id);
    }

    @Test
    void getPublishedEventInfo_NotPublished_ThrowsException() {
        Long id = 1L;
        Event event = new Event();
        event.setId(id);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.getPublishedEventInfo(id))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Event is not available");

        verify(eventRepository).findById(id);
    }

    @Test
    void getLockEventTierForUpdate_Success() {
        Long id = 1L;
        EventTier eventTier = new EventTier();
        eventTier.setQuantity(100);
        eventTier.setSoldCount(50);
        eventTier.setTier(TicketTier.VIP);

        EventTierBasicInfo expectedInfo = new EventTierBasicInfo(TicketTier.VIP, BigDecimal.valueOf(100), 100, 50);

        when(eventTierRepository.findByIdForUpdate(id)).thenReturn(Optional.of(eventTier));
        when(mapper.toEventTierBasicInfo(eventTier)).thenReturn(expectedInfo);

        EventTierBasicInfo result = eventService.getLockEventTierForUpdate(id);

        assertThat(result).isEqualTo(expectedInfo);
        verify(eventTierRepository).findByIdForUpdate(id);
        verify(mapper).toEventTierBasicInfo(eventTier);
    }

    @Test
    void getLockEventTierForUpdate_NotFound_ThrowsException() {
        Long id = 1L;
        when(eventTierRepository.findByIdForUpdate(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getLockEventTierForUpdate(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event tier not found");

        verify(eventTierRepository).findByIdForUpdate(id);
    }

    @Test
    void getLockEventTierForUpdate_OutOfStock_ThrowsException() {
        Long id = 1L;
        EventTier eventTier = new EventTier();
        eventTier.setQuantity(100);
        eventTier.setSoldCount(100);
        eventTier.setTier(TicketTier.VIP);

        when(eventTierRepository.findByIdForUpdate(id)).thenReturn(Optional.of(eventTier));

        assertThatThrownBy(() -> eventService.getLockEventTierForUpdate(id))
                .isInstanceOf(EventTierOutOfStockException.class)
                .hasMessageContaining("Event tier VIP is out of stock");

        verify(eventTierRepository).findByIdForUpdate(id);
        verifyNoInteractions(mapper);
    }

    @Test
    void batchIncrementEventTierSoldCount_CallsJdbcTemplate() {
        List<AdjustSoldCountData> adjustments = List.of(new AdjustSoldCountData(1L, 2));
        when(jdbcTemplate.batchUpdate(anyString(), anyCollection(), anyInt(), any(ParameterizedPreparedStatementSetter.class)))
                .thenReturn(new int[0][0]);

        eventService.batchIncrementEventTierSoldCount(adjustments);

        verify(jdbcTemplate).batchUpdate(anyString(), eq(adjustments), eq(1), any(ParameterizedPreparedStatementSetter.class));
    }

    @Test
    void batchDecrementEventTierSoldCount_CallsJdbcTemplate() {
        List<AdjustSoldCountData> adjustments = List.of(new AdjustSoldCountData(1L, 2));
        when(jdbcTemplate.batchUpdate(anyString(), anyCollection(), anyInt(), any(ParameterizedPreparedStatementSetter.class)))
                .thenReturn(new int[0][0]);

        eventService.batchDecrementEventTierSoldCount(adjustments);

        verify(jdbcTemplate).batchUpdate(anyString(), eq(adjustments), eq(1), any(ParameterizedPreparedStatementSetter.class));
    }
}
