package net.therap.kafkaregistry.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author tanvirhassan
 * @since 14/8/25
 */
@Service
public class KafkaTopicRegistrar {

    private final AdminClient adminClient;

    public KafkaTopicRegistrar(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.client-id}") String clientId) {

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("client.id", clientId);
        adminClient = AdminClient.create(props);
    }

    public void registerTopic(String topicName, int partitions, short replicationFactor) {
        try {
            Set<String> existingTopics = adminClient.listTopics().names().get();

            if (existingTopics.contains(topicName)) {
                System.out.println("Topic already exists: " + topicName);

                return;
            }

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            System.out.printf("Topic '%s' created with %d partitions and replication factor %d%n",
                    topicName, partitions, replicationFactor);
        } catch (Exception e) {
            if (e.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException) {
                System.err.println("Kafka brokers not reachable. Skipping topic creation.");
            } else {
                throw new RuntimeException("Failed to create topic: " + topicName, e);
            }
        }
    }
}
