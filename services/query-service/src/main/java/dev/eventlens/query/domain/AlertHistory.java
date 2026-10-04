package dev.eventlens.query.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="alert_history")
public class AlertHistory {
    @Id private UUID id;
    @Column(name="rule_id",nullable=false) private UUID ruleId;
    @Column(name="observed_count",nullable=false) private long observedCount;
    @Column(name="triggered_at",nullable=false) private Instant triggeredAt;
    protected AlertHistory() {}
    public AlertHistory(UUID id,UUID ruleId,long observedCount,Instant triggeredAt){this.id=id;this.ruleId=ruleId;this.observedCount=observedCount;this.triggeredAt=triggeredAt;}
    public UUID getId(){return id;} public UUID getRuleId(){return ruleId;} public long getObservedCount(){return observedCount;} public Instant getTriggeredAt(){return triggeredAt;}
}

