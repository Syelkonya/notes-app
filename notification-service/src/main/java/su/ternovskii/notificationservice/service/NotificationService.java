package su.ternovskii.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.dispatcher.NotificationDispatcher;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;
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

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        if (!notificationDispatcher.supports(notificationRequest.channel())) {
            throw new IllegalArgumentException("Unknown channel: " + notificationRequest.channel());
        }

        NotificationEntity entity = notificationPersistence.create(notificationRequest);
        log.info("Created notification id={} status=NEW", entity.getId());

        notificationDispatcher.dispatch(entity.getChannel(), entity.getMessage());
        entity = notificationPersistence.updateStatus(entity.getId(), NotificationStatus.SENT);
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
}
