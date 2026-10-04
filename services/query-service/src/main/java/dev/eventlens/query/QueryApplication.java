package dev.eventlens.query;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class QueryApplication {
    public static void main(String[] args) { SpringApplication.run(QueryApplication.class, args); }
}

