package net.therap.learningProcessor.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.therap.learningProcessor.entity.UpdateInfo;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
public class UpdateInfoDeserializer implements Deserializer<UpdateInfo> {

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public UpdateInfo deserialize(String topic, byte[] data) {
        try {
            return MAPPER.readValue(data, UpdateInfo.class);
        } catch (Exception e) {
            throw new SerializationException();
        }
    }
}
