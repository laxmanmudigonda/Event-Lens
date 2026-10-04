package dev.eventlens.ingestion.controller;

import dev.eventlens.ingestion.domain.ApiKey;
import dev.eventlens.ingestion.domain.Project;
import dev.eventlens.ingestion.repository.ApiKeyRepository;
import dev.eventlens.ingestion.repository.ProjectRepository;
import dev.eventlens.ingestion.security.ApiKeyHasher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/projects")
public class AdminController {
    public record CreateProject(@NotBlank String name) {}
    public record CreatedProject(UUID projectId, String name, String apiKey) {}
    private final ProjectRepository projects; private final ApiKeyRepository keys;
    private final ApiKeyHasher hasher; private final Clock clock; private final SecureRandom random = new SecureRandom();
    public AdminController(ProjectRepository projects, ApiKeyRepository keys, ApiKeyHasher hasher, Clock clock) {
        this.projects = projects; this.keys = keys; this.hasher = hasher; this.clock = clock;
    }
    @PostMapping
    @Transactional
    public CreatedProject create(@Valid @RequestBody CreateProject request) {
        UUID projectId = UUID.randomUUID();
        String rawKey = "evl_" + Base64.getUrlEncoder().withoutPadding().encodeToString(random.generateSeed(24));
        projects.save(new Project(projectId, request.name(), clock.instant()));
        keys.save(new ApiKey(UUID.randomUUID(), projectId, "default", rawKey.substring(0, 12), hasher.hash(rawKey), clock.instant()));
        return new CreatedProject(projectId, request.name(), rawKey);
    }
}

