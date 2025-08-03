package net.therap.app.service;

import net.therap.app.model.UpdateInfo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Service
public class UpdateInfoService {

    private final KafkaTemplate<String, UpdateInfo> kafkaTemplate;

    public UpdateInfoService(KafkaTemplate<String, UpdateInfo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUpdateInfo(UpdateInfo updateInfo) {
        kafkaTemplate.send("update-info-topic", updateInfo);
    }
}
