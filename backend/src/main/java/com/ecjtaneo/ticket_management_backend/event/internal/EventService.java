package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.internal.mapper.EventMapper;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventRepository;
import com.ecjtaneo.ticket_management_backend.event.internal.repository.EventTierRepository;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;

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

    @Transactional
    public MessageResponseDto createEvent(CreateEventDto dto, Long createdBy) {
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