package com.ecjtaneo.ticket_management_backend.event.controller;

import com.ecjtaneo.ticket_management_backend.event.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.service.EventService;
import com.ecjtaneo.ticket_management_backend.shared.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;

    @PostMapping
    public MessageResponseDto createEvent(@Valid @RequestBody CreateEventDto dto, @CurrentUserId Long userId) {
        return service.createEvent(dto, userId);
    }
}