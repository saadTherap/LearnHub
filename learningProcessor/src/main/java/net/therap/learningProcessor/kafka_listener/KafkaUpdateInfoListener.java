package net.therap.learningProcessor.kafka_listener;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import net.therap.learningProcessor.entity.UpdateInfo;
import net.therap.learningProcessor.service.UpdateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Component
public class KafkaUpdateInfoListener {

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    private final UpdateInfoService updateInfoService;

    public KafkaUpdateInfoListener(UpdateInfoService updateInfoService) {
        this.updateInfoService = updateInfoService;
    }

    @KafkaListener(
            topics = "${kafka.topics.update-info}",
            groupId = "${kafka.topics.update-info.grp}"
    )
    void listen(String json) {
        UpdateInfo updateInfo = producerConsumerTask.deserialize(json, UpdateInfo.class);
        updateInfoService.invalidateCache(updateInfo);
    }
}