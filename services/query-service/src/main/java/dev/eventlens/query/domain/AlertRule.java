package dev.eventlens.query.domain;
import dev.eventlens.model.LogLevel;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="alert_rules")
public class AlertRule {
    @Id private UUID id;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=100) private String service;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private LogLevel level;
    @Column(name="threshold_count",nullable=false) private int threshold;
    @Column(name="window_seconds",nullable=false) private int windowSeconds;
    @Column(nullable=false) private boolean enabled;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    protected AlertRule() {}
    public AlertRule(UUID id,String name,String service,LogLevel level,int threshold,int windowSeconds,Instant createdAt){this.id=id;this.name=name;this.service=service;this.level=level;this.threshold=threshold;this.windowSeconds=windowSeconds;this.enabled=true;this.createdAt=createdAt;}
    public UUID getId(){return id;} public String getName(){return name;} public String getService(){return service;} public LogLevel getLevel(){return level;} public int getThreshold(){return threshold;} public int getWindowSeconds(){return windowSeconds;} public boolean isEnabled(){return enabled;} public Instant getCreatedAt(){return createdAt;}
}

