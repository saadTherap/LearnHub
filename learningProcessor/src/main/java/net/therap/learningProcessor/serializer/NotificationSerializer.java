package net.therap.learningProcessor.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.therap.learningProcessor.entity.Notification;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
public class NotificationSerializer implements Serializer<Notification> {

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build();


    @Override
    public byte[] serialize(String topic, Notification data) {
        try {
            return MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
