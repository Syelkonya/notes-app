package su.ternovskii.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.model.Channel;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, Long> {

    Optional<NotificationTemplateEntity> findByChannel(Channel channel);

}
