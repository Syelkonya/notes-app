package su.ternovskii.emailadapter.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import su.ternovskii.emailadapter.dto.NotificationCommand;
import su.ternovskii.emailadapter.dto.NotificationResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {

    private final KafkaTemplate<String, NotificationResult> kafkaTemplate;

    @KafkaListener(topics = "notification.email.send", groupId = "email-adapter")
    public void handle(NotificationCommand command) {
        log.info("EMAIL adapter received: notificationId={}, recipient={}",
                command.notificationId(), command.recipient());

        // Эмуляция отправки EMAIL (всегда успех для EMAIL)
        boolean success = true;
        String error = null;

        log.info("EMAIL sent successfully for notificationId={}", command.notificationId());

        // Отправляем результат обратно в Kafka
        NotificationResult result = new NotificationResult(
                command.notificationId(),
                "EMAIL",
                success,
                error
        );
        kafkaTemplate.send("notification.result",
                String.valueOf(command.notificationId()), result);

        log.info("EMAIL result sent to notification.result: success={}", success);
    }
}
