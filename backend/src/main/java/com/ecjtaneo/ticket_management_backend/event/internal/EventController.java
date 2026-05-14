package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventRequestDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
class EventController {
    private final EventService service;

    @GetMapping("/{id}")
    EventInfoResponseDto getEventInfoById(@PathVariable Long id) {
        return service.getEventInfoById(id);
    }

    @GetMapping
    List<EventBasicInfoResponseDto> getEvents(@RequestParam(name = "cursor", required = false) Long lastSeenId) {
        if (lastSeenId == null)
            return service.getEvents();
        return service.getEvents(lastSeenId);
    }

    @PostMapping
    MessageResponseDto createEvent(@RequestPart("data") @Valid CreateEventRequestDto dto,
            @RequestPart("image") MultipartFile image, @CurrentUserId Long userId) throws IOException {
        return service.createEvent(dto, image, userId);
    }
}