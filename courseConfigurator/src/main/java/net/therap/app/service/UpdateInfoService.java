package net.therap.app.service;

import net.therap.app.model.UpdateInfo;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Service
public class UpdateInfoService {

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    @Value("${kafka.topics.update-info}")
    private String updateInfoTopic;

    public void sendUpdateInfo(UpdateInfo updateInfo) {
        producerConsumerTask.send(updateInfoTopic, updateInfo);
    }
}
