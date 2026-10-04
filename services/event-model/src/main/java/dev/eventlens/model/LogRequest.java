package dev.eventlens.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Map;

public record LogRequest(
        String eventId,
        Instant timestamp,
        @NotBlank @Size(max = 100) String service,
        @NotBlank @Size(max = 50) String environment,
        @NotNull LogLevel level,
        @NotBlank @Size(max = 16_384) String message,
        @Size(max = 200) String traceId,
        @Size(max = 200) String requestId,
        @Size(max = 255) String host,
        Map<String, Object> metadata) {
}

