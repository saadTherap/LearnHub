package net.therap.app.repository;

import net.therap.app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
