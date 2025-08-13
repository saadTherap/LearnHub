package net.therap.kafkaregistry.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.therap.kafkaregistry.model.Notification;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

/**
 * @author tanvirhassan
 * @since 13/8/25
 */
public class NotificationDeserializer implements Deserializer<Notification> {

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public Notification deserialize(String topic, byte[] data) {
        try {
            return MAPPER.readValue(data, Notification.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}