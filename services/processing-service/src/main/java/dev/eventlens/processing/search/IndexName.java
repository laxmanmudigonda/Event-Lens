package dev.eventlens.processing.search;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
public final class IndexName {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private IndexName() {}
    public static String forTimestamp(Instant timestamp) { return "eventlens-logs-" + DAY.format(timestamp); }
}

