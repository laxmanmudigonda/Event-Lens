package dev.eventlens.query.repository;
import dev.eventlens.query.domain.AlertHistory; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface AlertHistoryRepository extends JpaRepository<AlertHistory,UUID>{List<AlertHistory> findTop100ByOrderByTriggeredAtDesc();}

