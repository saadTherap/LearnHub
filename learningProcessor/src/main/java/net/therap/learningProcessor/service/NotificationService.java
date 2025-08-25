package net.therap.learningProcessor.service;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import net.therap.learningProcessor.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Service
public class NotificationService {

    @Value("${kafka.topics.notification}")
    private String notificationTopic;

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    public void sendNotification(Notification notification) {
        producerConsumerTask.send(notificationTopic, notification);
        System.out.println("Here at send notification");
    }
}