package dev.eventlens.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class EventFactory {
    private EventFactory() {}

    public static LogEvent from(LogRequest request, String projectId, Clock clock) {
        Instant now = clock.instant();
        return new LogEvent(
                blank(request.eventId()) ? UUID.randomUUID().toString() : request.eventId(),
                request.timestamp() == null ? now : request.timestamp(),
                now,
                projectId,
                request.service(),
                request.environment(),
                request.level(),
                request.message(),
                request.traceId(),
                request.requestId(),
                request.host(),
                request.metadata() == null ? Map.of() : Map.copyOf(request.metadata()));
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}

