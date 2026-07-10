package su.ternovskii.notificationservice.dto.response;

import su.ternovskii.notificationservice.model.DeliveryAttemptStatus;

import java.time.Instant;

public record DeliveryAttemptResponse(
    Long id,
    DeliveryAttemptStatus status,
    Instant createdAt
) {}