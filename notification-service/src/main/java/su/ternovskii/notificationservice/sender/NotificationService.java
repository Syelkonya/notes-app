package su.ternovskii.notificationservice.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.mapper.NotificationMapper;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.repository.NotificationRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    private final Map<Channel, NotificationSender> senders;
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    public NotificationService(List<NotificationSender> notificationSenderList,
                               NotificationMapper notificationMapper,
                               NotificationRepository notificationRepository) {
        this.senders = new EnumMap<>(Channel.class);
        notificationSenderList.forEach(s -> senders.put(s.channel(), s));
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
    }

    public ResponseEntity<NotificationResponse> sendNotification(NotificationRequest notificationRequest) {
        NotificationSender sender = senders.get(notificationRequest.channel());
        NotificationEntity notificationEntity = notificationMapper.toEntity(notificationRequest);
        notificationRepository.save(notificationEntity);
        if (sender == null) {
            throw new IllegalArgumentException("Unknown channel: " + notificationRequest.channel());
        }

        try {
            sender.send(notificationEntity.getMessage());
            notificationEntity.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notificationEntity);
        } catch (Exception e) {
            notificationEntity.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notificationEntity);
            throw e;
        }
        // аспект по обработке ошибок
        return ResponseEntity.accepted().body(notificationMapper.toResponse(notificationEntity));
    }
}
