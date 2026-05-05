package com.ecjtaneo.ticket_management_backend.event.service;

import com.ecjtaneo.ticket_management_backend.event.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.mapper.EventMapper;
import com.ecjtaneo.ticket_management_backend.event.model.Event;
import com.ecjtaneo.ticket_management_backend.event.model.EventTier;
import com.ecjtaneo.ticket_management_backend.event.repository.EventRepository;
import com.ecjtaneo.ticket_management_backend.event.repository.EventTierRepository;
import com.ecjtaneo.ticket_management_backend.shared.MessageResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventTierRepository eventTierRepository;
    private final EventMapper eventMapper;

    @Transactional
    public MessageResponseDto createEvent(CreateEventDto dto, Long createdBy) {
        Event event = eventMapper.toEvent(dto);
        event.setCreatedBy(createdBy);
        //event.setStatus(EventStatus.DRAFT); --- this is the default

        Event savedEvent = eventRepository.save(event);

        List<EventTier> tiers = dto.tiers().stream()
                .map(eventMapper::toEventTier)
                .peek(tier -> tier.setEvent(savedEvent))
                .toList();

        eventTierRepository.saveAll(tiers);

        return new MessageResponseDto("Event created successfully");
    }
}