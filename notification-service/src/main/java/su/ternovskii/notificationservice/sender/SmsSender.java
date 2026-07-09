package su.ternovskii.notificationservice.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.ternovskii.notificationservice.model.Channel;

@Slf4j
@Component
public class SmsSender implements NotificationSender{

    @Override
    public void send(String message) {
        log.info("sms was sent {}", message);
    }

    @Override
    public Channel channel() {
        return Channel.SMS;
    }
}
