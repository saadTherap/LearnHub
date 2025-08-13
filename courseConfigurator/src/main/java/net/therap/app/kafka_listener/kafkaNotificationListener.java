package net.therap.app.kafka_listener;

import net.therap.app.model.Notification;
import net.therap.app.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public kafkaNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-grp"
    )
    void listener(Notification notification) {
        logger.info("Received notification: {}", notification);
        notificationService.saveNotification(notification);
    }
}
