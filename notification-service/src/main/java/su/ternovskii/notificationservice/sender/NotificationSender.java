package su.ternovskii.notificationservice.sender;

import su.ternovskii.notificationservice.model.Channel;

public interface NotificationSender {
    void send(String message);
    Channel channel();
}