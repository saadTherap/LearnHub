package net.therap.app.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.therap.app.model.UpdateInfo;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
public class UpdateInfoSerializer implements Serializer<UpdateInfo> {

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build();


    @Override
    public byte[] serialize(String topic, UpdateInfo data) {
        try {
            return MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
