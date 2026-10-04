package dev.eventlens.model;

import java.time.Instant;

public record DeadLetterEvent(
        LogEvent originalEvent,
        String errorType,
        String errorMessage,
        String originalTopic,
        int partition,
        long offset,
        Instant failedAt,
        int retryCount) {
}

