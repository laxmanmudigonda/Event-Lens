package dev.eventlens.processing.search;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
class IndexNameTest {
    @Test void usesUtcCalendarDay() {
        assertEquals("eventlens-logs-2026-10-02", IndexName.forTimestamp(Instant.parse("2026-10-02T23:59:59Z")));
    }
}
