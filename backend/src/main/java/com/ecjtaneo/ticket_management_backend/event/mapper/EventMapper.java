package com.ecjtaneo.ticket_management_backend.event.mapper;

import com.ecjtaneo.ticket_management_backend.event.dto.CreateEventDto;
import com.ecjtaneo.ticket_management_backend.event.dto.CreateEventTierDto;
import com.ecjtaneo.ticket_management_backend.event.model.Event;
import com.ecjtaneo.ticket_management_backend.event.model.EventTier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tiers", ignore = true)
    Event toEvent(CreateEventDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventTier toEventTier(CreateEventTierDto dto);
}