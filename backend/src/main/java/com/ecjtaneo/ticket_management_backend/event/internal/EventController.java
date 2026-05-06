package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoDto;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;

    @GetMapping("/{id}")
    public List<EventInfoDto> getEventInfoById(@PathVariable Long id) {
        return service.getEventInfoById(id);
    }

    @GetMapping
    public List<EventBasicInfoDto> getEvents(@RequestParam(name = "cursor", required = false) Long lastSeenId) {
        if(lastSeenId == null) return service.getEvents();
        return service.getEvents(lastSeenId);
    }


    @PostMapping
    public MessageResponseDto createEvent(@Valid @RequestBody CreateEventDto dto, @CurrentUserId Long userId) {
        return service.createEvent(dto, userId);
    }
}