package su.ternovskii.emailadapter.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import su.ternovskii.emailadapter.dto.NotificationCommand;

@Slf4j
@Component
public class EmailListener {

    @KafkaListener(topics = "notification.email.send", groupId = "email-adapter")
    public void handle(NotificationCommand command) {
        log.info("[EMAIL] Received command: notificationId={}, recipient={}", command.notificationId(), command.recipient());
        log.info("[EMAIL] Sending email to {} with message: {}", command.recipient(), command.message());
    }
}
