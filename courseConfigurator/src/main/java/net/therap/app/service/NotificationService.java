package net.therap.app.service;

import jakarta.transaction.Transactional;
import net.therap.app.model.Notification;
import net.therap.app.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Service
public class NotificationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void saveNotification(Notification notification) {
        logger.info("-------------- Received and saving notification ---------------------");
        logger.info("{}", notification);
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification after saving: {}", savedNotification);
        logger.info("--------------- Saved notification ----------------------");
    }
}
