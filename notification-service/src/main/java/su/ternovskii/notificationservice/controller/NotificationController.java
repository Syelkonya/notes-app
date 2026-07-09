package su.ternovskii.notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.dto.response.NotificationResponse;
import su.ternovskii.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("")
    public ResponseEntity<NotificationResponse> createNote(
            @Valid @RequestBody NotificationRequest notificationRequest
    ) {
       NotificationResponse notificationResponse = notificationService.sendNotification(notificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationResponse);
    }
}
