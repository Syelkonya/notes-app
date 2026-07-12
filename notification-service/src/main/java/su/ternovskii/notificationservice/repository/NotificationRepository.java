package su.ternovskii.notificationservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import su.ternovskii.notificationservice.entity.NotificationEntity;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @EntityGraph(attributePaths = "deliveryAttempts")
     List<NotificationEntity> findAll();

    List<NotificationEntity> findByRecipientOrderByCreatedAtDesc(String recipient);

}
