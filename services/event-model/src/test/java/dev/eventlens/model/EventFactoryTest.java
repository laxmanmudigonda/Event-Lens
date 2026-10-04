package dev.eventlens.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EventFactoryTest {
    @Test
    void fillsServerManagedFields() {
        Instant now = Instant.parse("2026-10-02T12:31:22Z");
        var request = new LogRequest(null, null, "payments", "test", LogLevel.INFO, "ok", null, null, null, Map.of());
        var event = EventFactory.from(request, "project-1", Clock.fixed(now, ZoneOffset.UTC));
        assertNotNull(event.eventId());
        assertEquals(now, event.timestamp());
        assertEquals("project-1", event.projectId());
    }
}
