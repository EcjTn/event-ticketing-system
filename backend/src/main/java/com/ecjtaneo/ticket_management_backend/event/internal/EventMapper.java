package com.ecjtaneo.ticket_management_backend.event.internal;

import com.ecjtaneo.ticket_management_backend.event.EventTierBasicInfo;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventRequest;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.CreateEventTierRequest;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventBasicInfoResponse;
import com.ecjtaneo.ticket_management_backend.event.internal.dto.EventInfoResponse;
import com.ecjtaneo.ticket_management_backend.event.internal.model.Event;
import com.ecjtaneo.ticket_management_backend.event.internal.model.EventTier;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(CreateEventRequest dto);
    EventTier toEventTier(CreateEventTierRequest dto);
    List<EventBasicInfoResponse> toEventBasicInfoDtoList(List<Event> event);
    EventInfoResponse toEventInfoDto(Event event);
    EventTierBasicInfo toEventTierBasicInfo(EventTier eventTier);
}