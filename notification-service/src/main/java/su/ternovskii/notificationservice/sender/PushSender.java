package su.ternovskii.notificationservice.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.ternovskii.notificationservice.model.Channel;

@Slf4j
@Component
public class PushSender implements NotificationSender{

    @Override
    public void send(String message) {
        log.info("push was sent {}", message);
    }

    @Override
    public Channel channel() {
        return Channel.PUSH;
    }
}
