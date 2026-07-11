package su.ternovskii.notificationservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.repository.NotificationTemplateRepository;


@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Transactional
    public NotificationTemplateEntity updateText(Long id, String newText) {
        NotificationTemplateEntity notificationTemplateEntity = notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NotificationTemplate not found: " + id));
        notificationTemplateEntity.setText(newText);
        return notificationTemplateRepository.save(notificationTemplateEntity);
    }

}
