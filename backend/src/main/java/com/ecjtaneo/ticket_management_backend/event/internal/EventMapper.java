package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventRequestDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventTierRequestDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(CreateEventRequestDto dto);
    EventTier toEventTier(CreateEventTierRequestDto dto);
    List<EventBasicInfoResponseDto> toEventBasicInfoDtoList(List<Event> event);
    EventInfoResponseDto toEventInfoDto(Event event);
    EventTierBasicInfo toEventTierBasicInfo(EventTier eventTier);
}