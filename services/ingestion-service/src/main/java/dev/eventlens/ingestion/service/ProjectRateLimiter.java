package dev.eventlens.ingestion.service;

import dev.eventlens.ingestion.exception.RateLimitException;
import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectRateLimiter {
    private record Window(long minute, AtomicInteger count) {}
    private final ConcurrentHashMap<UUID, Window> windows = new ConcurrentHashMap<>();
    private final int limit;
    private final Clock clock;
    public ProjectRateLimiter(@Value("${eventlens.rate-limit-per-minute:1000}") int limit, Clock clock) {
        this.limit = limit; this.clock = clock;
    }
    public void acquire(UUID projectId, int permits) {
        long minute = clock.instant().getEpochSecond() / 60;
        Window window = windows.compute(projectId, (id, current) ->
                current == null || current.minute != minute ? new Window(minute, new AtomicInteger()) : current);
        if (window.count.addAndGet(permits) > limit) {
            window.count.addAndGet(-permits);
            throw new RateLimitException("Project rate limit exceeded: " + limit + " events/minute");
        }
    }
}

