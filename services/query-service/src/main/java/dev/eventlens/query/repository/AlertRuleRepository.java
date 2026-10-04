package dev.eventlens.query.repository;
import dev.eventlens.query.domain.AlertRule; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface AlertRuleRepository extends JpaRepository<AlertRule,UUID>{List<AlertRule> findByEnabledTrue();}

