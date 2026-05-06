package com.ecjtaneo.ticket_management_backend.event.internal.mapper;

import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventTierDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoDto;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(CreateEventDto dto);
    EventTier toEventTier(CreateEventTierDto dto);
    List<EventBasicInfoDto> toEventBasicInfoDtoList(List<Event> event);
    EventInfoDto toEventInfoDto(Event event);
}