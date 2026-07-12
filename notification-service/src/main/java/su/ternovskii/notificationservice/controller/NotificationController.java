package su.ternovskii.notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.DeliveryAttemptResponse;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping()
    public ResponseEntity<NotificationResponse> createNote(
            @Valid @RequestBody NotificationRequest notificationRequest
    ) {
       NotificationResponse notificationResponse = notificationService.sendNotification(notificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationResponse);
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<DeliveryAttemptResponse>> getAllAttemptsOfNotification(
            @PathVariable Long id
    ) {
        NotificationResponse notificationResponse = notificationService.getNotificationResponse(id);
        return ResponseEntity.status(HttpStatus.OK).body(notificationResponse.deliveryAttempts());
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        List<NotificationResponse> notifications = notificationService.getAll();
        // маппер обращается к getDeliveryAttempts() для каждой — вот тут N+1
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/by-recipient/{recipient}")
    public ResponseEntity<List<NotificationResponse>> getNotification(
            @PathVariable String recipient
    ) {
        List<NotificationResponse> notificationResponseList =
                notificationService.getNotificationResponseListByRecipientAndCreatedAtDesc(recipient);
        return ResponseEntity.status(HttpStatus.OK).body(notificationResponseList);
    }
}
