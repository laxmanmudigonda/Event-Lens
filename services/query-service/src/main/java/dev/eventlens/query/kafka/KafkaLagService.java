package dev.eventlens.query.kafka;
import java.util.*; import java.util.concurrent.TimeUnit; import org.apache.kafka.clients.admin.AdminClient; import org.apache.kafka.common.TopicPartition; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
@Service
public class KafkaLagService {
    public record PartitionLag(int partition,long endOffset,long committedOffset,long lag){}
    private final AdminClient admin; private final String topic; private final String group;
    public KafkaLagService(AdminClient admin,@Value("${eventlens.kafka.raw-topic}") String topic,@Value("${eventlens.kafka.consumer-group:eventlens-indexers}") String group){this.admin=admin;this.topic=topic;this.group=group;}
    public List<PartitionLag> lag() throws Exception {
        var description=admin.describeTopics(List.of(topic)).allTopicNames().get(5,TimeUnit.SECONDS).get(topic);
        var partitions=description.partitions().stream().map(p->new TopicPartition(topic,p.partition())).toList();
        var ends=admin.listOffsets(partitions.stream().collect(java.util.stream.Collectors.toMap(p->p, p->org.apache.kafka.clients.admin.OffsetSpec.latest()))).all().get(5,TimeUnit.SECONDS);
        var committed=admin.listConsumerGroupOffsets(group).partitionsToOffsetAndMetadata().get(5,TimeUnit.SECONDS);
        return partitions.stream().map(p->{long end=ends.get(p).offset();long current=committed.get(p)==null?0:committed.get(p).offset();return new PartitionLag(p.partition(),end,current,Math.max(0,end-current));}).toList();
    }
}

