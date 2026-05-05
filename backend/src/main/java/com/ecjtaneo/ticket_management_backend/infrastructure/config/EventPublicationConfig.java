package com.ecjtaneo.ticket_management_backend.infrastructure.config;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class EventPublicationConfig {
    private final IncompleteEventPublications incompleteEventPublications;
    private final CompletedEventPublications completedEventPublications;

    @Scheduled(fixedDelay = 5 * 60_000) // 5min
    public void resubmitIncompleteEvents() {
        incompleteEventPublications.resubmitIncompletePublicationsOlderThan(Duration.ofMinutes(1));
    }

    @Scheduled(fixedDelay = 24 * 60_000) // 24h
    public void clearCompletedPublications() {
        completedEventPublications.deletePublicationsOlderThan(Duration.ofDays(7));
    }

}
