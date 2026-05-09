package com.ecjtaneo.ticket_management_backend.event;

public interface EventApi {
    public void validateEventIsPublished(Long id);

    public EventTierBasicInfo getEventTierInfo(Long id);

    public void incrementEventTierSoldCount(Long tierId, int quantity);

    public void decrementEventTierSoldCount(Long tierId, int quantity);
}
