package dev.eventlens.query.alert;

import dev.eventlens.query.domain.AlertHistory;
import dev.eventlens.query.repository.*;
import dev.eventlens.query.search.OpenSearchGateway;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertEvaluator {
    private static final Logger log=LoggerFactory.getLogger(AlertEvaluator.class);
    private final AlertRuleRepository rules; private final AlertHistoryRepository history; private final OpenSearchGateway search;
    public AlertEvaluator(AlertRuleRepository rules,AlertHistoryRepository history,OpenSearchGateway search){this.rules=rules;this.history=history;this.search=search;}
    @Scheduled(fixedDelayString="${eventlens.alerts.interval-ms:60000}")
    public void evaluate(){
        Instant now=Instant.now();
        for(var rule:rules.findByEnabledTrue()){
            try { long count=search.count(rule.getService(),rule.getLevel().name(),now.minusSeconds(rule.getWindowSeconds()));
                if(count>=rule.getThreshold()){history.save(new AlertHistory(UUID.randomUUID(),rule.getId(),count,now));log.warn("alert triggered rule={} count={}",rule.getName(),count);}
            } catch(Exception ex){log.error("alert evaluation failed for rule={}: {}",rule.getId(),ex.getMessage());}
        }
    }
}

