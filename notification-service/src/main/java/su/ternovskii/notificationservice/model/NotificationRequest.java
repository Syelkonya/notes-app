package su.ternovskii.notificationservice.model;

import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotNull Channel channel,
        @NotNull String message) {
}