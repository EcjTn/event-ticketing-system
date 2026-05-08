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

    @Scheduled(fixedDelay = Duration.ofMinutes(5).toMillis())
    public void resubmitIncompleteEvents() {
        incompleteEventPublications.resubmitIncompletePublicationsOlderThan(Duration.ofMinutes(1));
    }

    @@Scheduled(fixedDelay = Duration.ofHours(24).toMillis())
    public void clearCompletedPublications() {
        completedEventPublications.deletePublicationsOlderThan(Duration.ofDays(7));
    }

}
