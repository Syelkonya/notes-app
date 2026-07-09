package su.ternovskii.notificationservice.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.mapper.NotificationMapper;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationPersistence {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationEntity create(NotificationRequest notificationRequest) {
        NotificationEntity notificationEntity = notificationMapper.toEntity(notificationRequest);
        return notificationRepository.save(notificationEntity);
    }

    @Transactional
    public NotificationEntity updateStatus(Long id, NotificationStatus notificationStatus) {
        NotificationEntity notificationEntity = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + id));
        notificationEntity.setStatus(notificationStatus);
        return notificationRepository.save(notificationEntity);
    }
}
