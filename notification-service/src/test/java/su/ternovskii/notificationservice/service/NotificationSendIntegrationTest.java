package su.ternovskii.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import su.ternovskii.notificationservice.dto.request.NotificationRequest;
import su.ternovskii.notificationservice.entity.NotificationEntity;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.model.NotificationStatus;
import su.ternovskii.notificationservice.repository.NotificationRepository;
import su.ternovskii.notificationservice.repository.NotificationTemplateRepository;

import java.util.List;

@Slf4j
@Testcontainers
@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
class NotificationSendIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    NotificationTemplateRepository notificationTemplateRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        notificationTemplateRepository.deleteAll();

        NotificationTemplateEntity template = new NotificationTemplateEntity();
        template.setChannel(Channel.PUSH);
        template.setText("Уведомление: {message}");

        notificationTemplateRepository.save(template);
    }

    @Test
    void sendingMessageAndRollBackTest() {
        notificationService.sendNotification(new NotificationRequest(
                Channel.PUSH,
                "dydya fyodr",
                "How do yo dodo?"));
        List<NotificationEntity> notificationEntityList = notificationRepository.findAll();
        Assertions.assertEquals(1, notificationEntityList.size());
        Assertions.assertEquals(NotificationStatus.SENT, notificationEntityList.getFirst().getStatus());

    }

    @Test
    void rollbackTransactionTest() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            transactionTemplate.execute(status -> {
                NotificationEntity entity = new NotificationEntity();
                entity.setChannel(Channel.PUSH);
                entity.setRecipient("test");
                entity.setMessage("rollback test {message}");
                entity.setStatus(NotificationStatus.NEW);
                entity.setRetryCount(0);
                notificationRepository.save(entity);
                throw new RuntimeException("демо-откат");
            });
        });

        Assertions.assertEquals(0, notificationRepository.count());
    }

}