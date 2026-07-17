package su.ternovskii.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.dispatcher.NotificationDispatcher;
import su.ternovskii.notificationservice.dto.kafka.NotificationCommand;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.kafka.NotificationKafkaProducer;
import su.ternovskii.notificationservice.mapper.NotificationMapper;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.persistence.NotificationPersistence;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationMapper notificationMapper;
    private final NotificationDispatcher notificationDispatcher;
    private final NotificationPersistence notificationPersistence;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationKafkaProducer notificationKafkaProducer;

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        NotificationEntity entity = notificationPersistence.create(notificationRequest);
        log.info("Created notification id={} status=NEW", entity.getId());

        NotificationTemplateEntity template =
                notificationTemplateService.getByChannel(notificationRequest.channel());

        String renderedMessage = template.getText().replace("{message}", entity.getMessage());

        NotificationCommand command = new NotificationCommand(
                entity.getId(),
                template.getChannel(),
                entity.getRecipient(),
                renderedMessage
        );

        notificationKafkaProducer.sendCommand(command);
        log.info("Command sent to Kafka for notificationId={}, channel={}",
                entity.getId(), template.getChannel());

        return notificationMapper.toResponse(entity);
    }

    public NotificationResponse getNotificationResponse(Long id) {
        NotificationEntity notificationEntity = notificationPersistence.get(id);
        return notificationMapper.toResponse(notificationEntity);
    }

    public List<NotificationResponse> getAll() {
        List<NotificationEntity> notifications = notificationPersistence.findAll();
        return notificationMapper.toResponseList(notifications);
    }

    public List<NotificationResponse> getNotificationResponseListByRecipientAndCreatedAtDesc(String recipient) {
        List<NotificationEntity> notifications = notificationPersistence.findByRecipientOrderByCreatedAtDesc(recipient);
        return notificationMapper.toResponseList(notifications);
    }

    public void retryPendingNotifications(int maxRetries) {
        List<NotificationEntity> pending = notificationPersistence.findPendingForRetry(NotificationStatus.NEW, maxRetries);
        log.info("Retry scheduler: found {} pending notifications", pending.size());

        for (NotificationEntity n : pending) {
            try {
                notificationDispatcher.dispatch(n.getChannel(), n.getMessage());
                notificationPersistence.updateStatus(n.getId(), NotificationStatus.SENT);
            } catch (Exception e) {
                log.info(e.getMessage());
                notificationPersistence.registerFailedAttempt(n.getId(), maxRetries);
            }
        }
    }

}
