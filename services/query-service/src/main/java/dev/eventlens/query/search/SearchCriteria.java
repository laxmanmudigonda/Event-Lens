package dev.eventlens.query.search;
import dev.eventlens.model.LogLevel;
import java.time.Instant;
public record SearchCriteria(String q, String service, String environment, LogLevel level,
        Instant from, Instant to, String traceId, int page, int size, String sort) {}

