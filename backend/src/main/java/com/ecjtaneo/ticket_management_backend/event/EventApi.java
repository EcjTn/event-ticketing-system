package com.ecjtaneo.ticket_management_backend.event;

import java.util.List;

public interface EventApi {
    public EventBasicInfo getPublishedEventInfo(Long id);

    public EventTierBasicInfo lockEventTierForUpdate(Long id);

    public void batchDecrementEventTierSoldCount(List<AdjustSoldCountData> adjustments);

    public void batchIncrementEventTierSoldCount(List<AdjustSoldCountData> adjustments);

}
