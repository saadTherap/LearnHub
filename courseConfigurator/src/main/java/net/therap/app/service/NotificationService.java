package net.therap.app.service;

import jakarta.transaction.Transactional;
import net.therap.app.model.Notification;
import net.therap.app.repository.NotificationRepository;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void saveNotification(Notification notification) {
        this.notificationRepository.save(notification);
    }
}
