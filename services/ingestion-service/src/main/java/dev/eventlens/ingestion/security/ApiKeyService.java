package dev.eventlens.ingestion.security;

import dev.eventlens.ingestion.domain.ApiKey;
import dev.eventlens.ingestion.exception.UnauthorizedException;
import dev.eventlens.ingestion.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repository;
    private final ApiKeyHasher hasher;
    public ApiKeyService(ApiKeyRepository repository, ApiKeyHasher hasher) { this.repository = repository; this.hasher = hasher; }

    public ApiKey authenticate(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) throw new UnauthorizedException("Missing X-API-Key header");
        return repository.findByKeyHashAndEnabledTrue(hasher.hash(rawKey))
                .orElseThrow(() -> new UnauthorizedException("Invalid API key"));
    }
}

