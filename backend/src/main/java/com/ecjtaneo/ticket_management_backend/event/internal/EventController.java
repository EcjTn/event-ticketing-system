package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventRequest;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoResponse;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoResponse;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventStatus;
import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
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
    EventInfoResponse getEventInfoById(@PathVariable Long id) {
        return service.getEventInfoById(id);
    }

    @GetMapping
    List<EventBasicInfoResponse> getEvents(@RequestParam(name = "cursor", required = false) Long lastSeenId) {
        if (lastSeenId == null)
            return service.getEvents();
        return service.getEvents(lastSeenId);
    }

    @PostMapping
    MessageResponse createEvent(@RequestPart("data") @Valid CreateEventRequest dto,
                                @RequestPart("image") MultipartFile image, @CurrentUserId Long userId) throws IOException {
        return service.createEvent(dto, image, userId);
    }



    //FOR ADMIN USE ONLY

    @PatchMapping("/{id}/cancel")
    MessageResponse cancelEvent(@PathVariable Long id) {
        return service.updateEventStatus(id, EventStatus.CANCELLED);
    }

    @PatchMapping("/{id}/publish")
    MessageResponse publishEvent(@PathVariable Long id) {
        return service.updateEventStatus(id, EventStatus.PUBLISHED);
    }

    @PatchMapping("/{id}/complete")
    MessageResponse completeEvent(@PathVariable Long id) {
        return service.updateEventStatus(id, EventStatus.COMPLETED);
    }

}