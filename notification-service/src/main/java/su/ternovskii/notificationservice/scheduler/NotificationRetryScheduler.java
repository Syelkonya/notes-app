package su.ternovskii.notificationservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.ternovskii.notificationservice.dispatcher.NotificationDispatcher;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.persistence.NotificationPersistence;
import su.ternovskii.notificationservice.repository.NotificationRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private static final int MAX_RETRIES = 5;

    private final NotificationRepository notificationRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final NotificationPersistence notificationPersistence; // транзакции — через другой бин

    @Scheduled(fixedDelay = 60_000)
    public void retryPendingNotifications() {
        List<NotificationEntity> pending =
                notificationRepository.findByStatusAndRetryCountLessThan(NotificationStatus.NEW, MAX_RETRIES);
        log.info("Retry scheduler: found {} pending notifications", pending.size());

        for (NotificationEntity n : pending) {
            try {
                notificationDispatcher.dispatch(n.getChannel(), n.getMessage());
                notificationPersistence.updateStatus(n.getId(), NotificationStatus.SENT);
            } catch (Exception e) {
                log.info(e.getMessage());
                notificationPersistence.registerFailedAttempt(n.getId(), MAX_RETRIES);
            }
        }
    }
}
