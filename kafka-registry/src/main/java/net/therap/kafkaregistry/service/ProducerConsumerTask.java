package net.therap.kafkaregistry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 14/8/25
 */
@Service
public class ProducerConsumerTask {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProducerConsumerTask(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, json);
            System.out.println("Sent: " + json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(String json, Class<T> classType) {
        try {
            return objectMapper.readValue(json, classType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON into " + classType.getName(), e);
        }
    }
}
