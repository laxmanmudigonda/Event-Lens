package dev.eventlens.ingestion.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {
    @Id private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(nullable = false, length = 100) private String name;
    @Column(name = "key_prefix", nullable = false, length = 16) private String keyPrefix;
    @Column(name = "key_hash", nullable = false, unique = true, length = 64) private String keyHash;
    @Column(nullable = false) private boolean enabled;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected ApiKey() {}
    public ApiKey(UUID id, UUID projectId, String name, String keyPrefix, String keyHash, Instant createdAt) {
        this.id = id; this.projectId = projectId; this.name = name; this.keyPrefix = keyPrefix;
        this.keyHash = keyHash; this.enabled = true; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getKeyHash() { return keyHash; }
    public boolean isEnabled() { return enabled; }
}

