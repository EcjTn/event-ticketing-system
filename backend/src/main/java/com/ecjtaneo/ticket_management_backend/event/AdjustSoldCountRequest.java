package com.ecjtaneo.ticket_management_backend.event;

public record AdjustSoldCountRequest(Long tierId, Integer quantity) {

}
