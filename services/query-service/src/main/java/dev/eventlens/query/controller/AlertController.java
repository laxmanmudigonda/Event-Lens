package dev.eventlens.query.controller;
import dev.eventlens.model.LogLevel; import dev.eventlens.query.domain.*; import dev.eventlens.query.repository.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/alerts")
public class AlertController {
    public record CreateAlert(@NotBlank String name,@NotBlank String service,@NotNull LogLevel level,@Min(1) int threshold,@Min(60) @Max(86400) int windowSeconds){}
    private final AlertRuleRepository rules; private final AlertHistoryRepository history;
    public AlertController(AlertRuleRepository rules,AlertHistoryRepository history){this.rules=rules;this.history=history;}
    @GetMapping public List<AlertRule> rules(){return rules.findAll();}
    @PostMapping public AlertRule create(@Valid @RequestBody CreateAlert r){return rules.save(new AlertRule(UUID.randomUUID(),r.name(),r.service(),r.level(),r.threshold(),r.windowSeconds(),Instant.now()));}
    @DeleteMapping("/{id}") public void delete(@PathVariable UUID id){rules.deleteById(id);}
    @GetMapping("/history") public List<AlertHistory> history(){return history.findTop100ByOrderByTriggeredAtDesc();}
}

