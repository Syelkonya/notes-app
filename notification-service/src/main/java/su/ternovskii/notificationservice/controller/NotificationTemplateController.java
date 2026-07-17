package su.ternovskii.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.service.NotificationTemplateService;

/**
 * Эндпоинт для проверки кеширования
 */
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @PutMapping("/{id}")
    public ResponseEntity<NotificationTemplateEntity> updateText(
            @PathVariable Long id,
            @RequestBody String newText
    ) {
        return ResponseEntity.ok(notificationTemplateService.updateText(id, newText));
    }
}