package net.therap.app.kafka_listener;

import net.therap.app.model.Notification;
import net.therap.app.service.NotificationService;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Component
public class kafkaNotificationListener {

    private final Logger logger = LoggerFactory.getLogger(kafkaNotificationListener.class);
    private final NotificationService notificationService;

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    public kafkaNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${kafka.topics.notification}",
            groupId = "${kafka.topics.notification.grp}"
    )
    void listener(String json) {
        Notification notification = producerConsumerTask.deserialize(json, Notification.class);
        logger.info("Received notification: {}", notification);
        notificationService.saveNotification(notification);
    }
}
