package su.ternovskii.notificationservice.dto.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ternovskii.notificationservice.dto.kafka.NotificationResult;
import su.ternovskii.notificationservice.entity.ChannelDeliveryEntity;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.model.DeliveryStatus;
import su.ternovskii.notificationservice.repository.ChannelDeliveryRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultListener {

    private final ChannelDeliveryRepository channelDeliveryRepository;

    @Transactional
    @KafkaListener(topics = "notification.result", groupId = "notification-service")
    public void handleResult(NotificationResult result) {
        log.info("Received result: notificationId={}, channel={}, success={}",
                result.notificationId(), result.channel(), result.success());

        // Парсим channel из строки
        Channel channel = Channel.valueOf(result.channel());

        // Ищем запись ChannelDelivery
        ChannelDeliveryEntity delivery = channelDeliveryRepository
                .findByNotificationIdAndChannel(result.notificationId(), channel)
                .orElse(null);

        if (delivery == null) {
            log.warn("ChannelDelivery not found for notificationId={}, channel={}",
                    result.notificationId(), result.channel());
            return;
        }

        if (result.success()) {
            // Успех — ставим SENT, retry не нужен
            delivery.setStatus(DeliveryStatus.SENT);
            delivery.setNextRetryAt(null);
            log.info("Channel {} SENT for notificationId={}", channel, result.notificationId());
        } else {
            // Неудача — ставим FAILED, планируем retry через 1 минуту
            delivery.setStatus(DeliveryStatus.FAILED);
            delivery.setRetryCount(delivery.getRetryCount() + 1);
            delivery.setNextRetryAt(Instant.now().plusSeconds(60));
            log.warn("Channel {} FAILED for notificationId={}, retry #{} scheduled",
                    channel, result.notificationId(), delivery.getRetryCount());
        }

        channelDeliveryRepository.save(delivery);
    }
}