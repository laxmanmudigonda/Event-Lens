package dev.eventlens.query.controller;
import dev.eventlens.query.kafka.KafkaLagService; import java.util.List; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/system") public class SystemController {private final KafkaLagService kafka;public SystemController(KafkaLagService kafka){this.kafka=kafka;}@GetMapping("/kafka")public List<KafkaLagService.PartitionLag> kafka() throws Exception{return kafka.lag();}}

