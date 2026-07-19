package su.ternovskii.pushadapter.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import su.ternovskii.pushadapter.dto.NotificationCommand;

@Slf4j
@Component
public class PushListener {

    @KafkaListener(topics = "notification.push.send", groupId = "push-adapter")
    public void handle(NotificationCommand command) {
        log.info("[PUSH] Received command: notificationId={}, recipient={}", command.notificationId(), command.recipient());
        log.info("[PUSH] Sending push notification to {} with message: {}", command.recipient(), command.message());
    }
}
