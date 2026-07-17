package su.ternovskii.notificationservice.dto.response;

import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.model.NotificationStatus;

import java.time.Instant;
import java.util.List;

public record NotificationResponse(
        Long id,
        Channel channel,
        String recipient,
        String message,
        NotificationStatus status,
        Instant createdAt,
        Long version,
        List<DeliveryAttemptResponse> deliveryAttempts
) {
}
