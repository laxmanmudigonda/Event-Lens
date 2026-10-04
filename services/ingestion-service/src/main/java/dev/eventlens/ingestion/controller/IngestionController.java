package dev.eventlens.ingestion.controller;

import dev.eventlens.ingestion.security.ApiKeyService;
import dev.eventlens.ingestion.service.LogIngestionService;
import dev.eventlens.ingestion.service.ProjectRateLimiter;
import dev.eventlens.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/logs")
public class IngestionController {
    private final ApiKeyService apiKeys;
    private final ProjectRateLimiter limiter;
    private final LogIngestionService service;
    public IngestionController(ApiKeyService apiKeys, ProjectRateLimiter limiter, LogIngestionService service) {
        this.apiKeys = apiKeys; this.limiter = limiter; this.service = service;
    }
    @PostMapping
    public CompletableFuture<ResponseEntity<AcceptedEvent>> ingest(
            @RequestHeader("X-API-Key") String rawKey, @Valid @RequestBody LogRequest request) {
        var key = apiKeys.authenticate(rawKey); limiter.acquire(key.getProjectId(), 1);
        return service.ingest(request, key.getProjectId().toString())
                .thenApply(body -> ResponseEntity.accepted().body(body));
    }
    @PostMapping("/batch")
    public CompletableFuture<ResponseEntity<List<AcceptedEvent>>> batch(
            @RequestHeader("X-API-Key") String rawKey,
            @RequestBody @NotEmpty @Size(max = 500) List<@Valid LogRequest> requests) {
        var key = apiKeys.authenticate(rawKey); limiter.acquire(key.getProjectId(), requests.size());
        return service.ingestBatch(requests, key.getProjectId().toString())
                .thenApply(body -> ResponseEntity.accepted().body(body));
    }
}

