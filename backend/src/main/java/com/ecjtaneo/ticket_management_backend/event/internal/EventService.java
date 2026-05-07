package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventRequestDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.event.internal.mapper.EventMapper;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventStatus;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventTierRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventTierRepository eventTierRepository;
    private final EventMapper mapper;


    public List<EventBasicInfoResponseDto> getEvents() {
        return mapper.toEventBasicInfoDtoList(
            eventRepository.findTop10ByStatusOrderByIdDesc(EventStatus.PUBLISHED)
        );
    }

    public List<EventBasicInfoResponseDto> getEvents(Long lastSeenId) {
        return mapper.toEventBasicInfoDtoList(
            eventRepository.findTop10ByIdLessThanOrderByIdDesc(lastSeenId)
        );
    }

    public List<EventInfoResponseDto> getEventInfoById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        EventInfoResponseDto eventInfoResponseDto = mapper.toEventInfoDto(event);
        
        Integer totalAvailableTickets = event.getTiers().stream()
            .mapToInt(tier -> tier.getQuantity() - tier.getSoldCount())
            .sum();
        
        return List.of(new EventInfoResponseDto(
            eventInfoResponseDto.id(),
            eventInfoResponseDto.name(),
            eventInfoResponseDto.date(),
            eventInfoResponseDto.venue(),
            eventInfoResponseDto.description(),
            eventInfoResponseDto.imageUrl(),
            eventInfoResponseDto.status(),
            eventInfoResponseDto.createdAt(),
            eventInfoResponseDto.tiers(),
            totalAvailableTickets
        ));
    }

    @Transactional
    public MessageResponseDto createEvent(CreateEventRequestDto dto, Long createdBy) {
        Event event = mapper.toEvent(dto);
        event.setCreatedBy(createdBy);
        //event.setStatus(EventStatus.DRAFT); --- this is the default

        Event savedEvent = eventRepository.save(event);

        List<EventTier> tiers = dto.tiers().stream()
                .map(mapper::toEventTier)
                .peek(tier -> tier.setEvent(savedEvent))
                .toList();

        eventTierRepository.saveAll(tiers);

        return new MessageResponseDto("Event created successfully");
    }
}