package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketInfoResponse;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketInfoResponse toTicketInfoResponse(Ticket ticket);
}
