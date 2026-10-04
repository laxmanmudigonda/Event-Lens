package dev.eventlens.ingestion.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected Project() {}
    public Project(UUID id, String name, Instant createdAt) { this.id = id; this.name = name; this.createdAt = createdAt; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
}

