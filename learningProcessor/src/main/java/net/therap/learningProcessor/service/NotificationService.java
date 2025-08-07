package net.therap.learningProcessor.service;

import net.therap.learningProcessor.entity.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Service
public class NotificationService {

    private final KafkaTemplate<String , Notification> kafkaTemplate;

    @Value("${kafka.topics.notification}")
    private String notificationTopic;

    public NotificationService(KafkaTemplate<String , Notification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(Notification notification) {
        this.kafkaTemplate.send(notificationTopic, notification);
    }
}
