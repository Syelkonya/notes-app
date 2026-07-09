package su.ternovskii.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import su.ternovskii.notificationservice.dispatcher.NotificationDispatcher;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.mapper.NotificationMapper;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.persistence.NotificationPersistence;
import su.ternovskii.notificationservice.repository.NotificationRepository;
import su.ternovskii.notificationservice.sender.NotificationSender;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationMapper notificationMapper;
    private final NotificationDispatcher notificationDispatcher;
    private final NotificationPersistence notificationPersistence ;

    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        if (!notificationDispatcher.supports(notificationRequest.channel())) {
            throw new IllegalArgumentException("Unknown channel: " + notificationRequest.channel());
        }

        NotificationEntity entity = notificationPersistence.create(notificationRequest);
        log.info("Created notification id={}", entity.getId());

        try {
            notificationDispatcher.dispatch(entity.getChannel(), entity.getMessage());
            entity = notificationPersistence.updateStatus(entity.getId(), NotificationStatus.SENT);
            log.info("Sent notification id={}", entity.getId());
        } catch (Exception e) {
            entity = notificationPersistence.updateStatus(entity.getId(), NotificationStatus.FAILED);
            log.error("Failed notification id={}", entity.getId(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Sending failed", e);
        }

        return notificationMapper.toResponse(entity);

    }

}
