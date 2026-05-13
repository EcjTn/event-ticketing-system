package com.ecjtaneo.ticket_management_backend.event;

import java.util.List;

public interface EventApi {
    public void validateEventIsPublished(Long id);

    public EventTierBasicInfo getEventTierInfo(Long id);

    public void batchDecrementEventTierSoldCount(List<EventTierQuantityAdjustment> adjustments);

    public void batchIncrementEventTierSoldCount(List<EventTierQuantityAdjustment> adjustments);

}
