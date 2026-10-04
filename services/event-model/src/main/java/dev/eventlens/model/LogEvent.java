package dev.eventlens.model;

import java.time.Instant;
import java.util.Map;

public record LogEvent(
        String eventId,
        Instant timestamp,
        Instant receivedAt,
        String projectId,
        String service,
        String environment,
        LogLevel level,
        String message,
        String traceId,
        String requestId,
        String host,
        Map<String, Object> metadata) {
}

