package su.ternovskii.smsadapter.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import su.ternovskii.smsadapter.dto.NotificationCommand;

@Slf4j
@Component
public class SmsListener {

    @KafkaListener(topics = "notification.sms.send", groupId = "sms-adapter")
    public void handle(NotificationCommand command) {
        log.info("[SMS] Received command: notificationId={}, recipient={}", command.notificationId(), command.recipient());
        log.info("[SMS] Sending SMS to {} with message: {}", command.recipient(), command.message());
    }
}
