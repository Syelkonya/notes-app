package su.ternovskii.notificationservice.sender;

import org.springframework.stereotype.Service;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.model.NotificationRequest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final Map<Channel, NotificationSender> senders;

    public NotificationService(List<NotificationSender> notificationSenderList) {
        this.senders = new EnumMap<>(Channel.class);
        notificationSenderList.forEach(s -> senders.put(s.channel(), s));
    }

    public void sendNotification(NotificationRequest request) {
        NotificationSender sender = senders.get(request.channel());
        if (sender == null) {
            throw new IllegalArgumentException("Unknown channel: " + request.channel());
        }
        sender.send(request.message());
    }
}
