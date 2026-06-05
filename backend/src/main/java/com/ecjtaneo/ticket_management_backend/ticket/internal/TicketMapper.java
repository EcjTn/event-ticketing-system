package com.ecjtaneo.ticket_management_backend.ticket.internal;

import com.ecjtaneo.ticket_management_backend.ticket.internal.dto.TicketInfoResponse;
import com.ecjtaneo.ticket_management_backend.ticket.internal.model.Ticket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    List<TicketInfoResponse> toTicketInfoResponse(List<Ticket> tickets);
}
