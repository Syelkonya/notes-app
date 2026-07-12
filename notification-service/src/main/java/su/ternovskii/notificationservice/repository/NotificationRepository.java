package su.ternovskii.notificationservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.model.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @EntityGraph(attributePaths = "deliveryAttempts")
     List<NotificationEntity> findAll();

    List<NotificationEntity> findByRecipientOrderByCreatedAtDesc(String recipient);

    List<NotificationEntity> findByStatusAndRetryCountLessThan(NotificationStatus notificationStatus, int maxRetries);
}
