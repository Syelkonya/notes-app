package su.ternovskii.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import su.ternovskii.notificationservice.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {



}
