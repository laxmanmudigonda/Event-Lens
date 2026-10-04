package dev.eventlens.ingestion.repository;
import dev.eventlens.ingestion.domain.ApiKey;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKeyHashAndEnabledTrue(String keyHash);
}

