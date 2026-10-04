package dev.eventlens.query.config;

import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestClient;

@Configuration
public class InfrastructureConfig {
    @Bean RestClient openSearch(@Value("${eventlens.opensearch.url}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }
    @Bean(destroyMethod = "close") AdminClient kafkaAdminClient(@Value("${spring.kafka.bootstrap-servers}") String servers) {
        return AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers));
    }
}

