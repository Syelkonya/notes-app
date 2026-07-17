package su.ternovskii.notificationservice.dto.kafka;

import su.ternovskii.notificationservice.model.Channel;

public record NotificationCommand(
    Long notificationId,
    Channel channel,
    String recipient,
    String message
) {}