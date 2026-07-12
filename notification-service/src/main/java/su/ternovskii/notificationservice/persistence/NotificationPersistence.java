package su.ternovskii.notificationservice.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.mapper.NotificationMapper;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationPersistence {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationEntity create(NotificationRequest notificationRequest) {
        NotificationEntity notificationEntity = notificationMapper.toEntity(notificationRequest);
        return notificationRepository.save(notificationEntity);
    }

    @Transactional
    public NotificationEntity updateStatus(Long id, NotificationStatus notificationStatus) {
        // get в этой же транзакции - целевое решение
        NotificationEntity notificationEntity = get(id);
        notificationEntity.setStatus(notificationStatus);
        return notificationRepository.save(notificationEntity);
    }

    @Transactional
    public NotificationEntity get(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + id));
    }

    public List<NotificationEntity> findAll() {
        return notificationRepository.findAll();
    }

    public List<NotificationEntity> findByRecipientOrderByCreatedAtDesc(String recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }
}
