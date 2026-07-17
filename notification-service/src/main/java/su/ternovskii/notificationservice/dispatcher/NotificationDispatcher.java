package su.ternovskii.notificationservice.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.sender.NotificationSender;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationDispatcher {

    private final Map<Channel, NotificationSender> senders;

    public NotificationDispatcher(List<NotificationSender> notificationSenderList) {
        this.senders = new EnumMap<>(Channel.class);
        notificationSenderList.forEach(s -> senders.put(s.channel(), s));
    }

    public boolean supports(Channel channel) {
        return senders.containsKey(channel);
    }

    //    отправка сообщения
    public void dispatch(Channel channel, String message) {
        senders.get(channel).send(message);
    }
}
