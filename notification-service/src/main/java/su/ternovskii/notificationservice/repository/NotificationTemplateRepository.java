package su.ternovskii.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, Long> {
}
