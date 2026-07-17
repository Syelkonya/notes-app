package su.ternovskii.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;
import su.ternovskii.notificationservice.entity.NotificationTemplateEntity;
import su.ternovskii.notificationservice.model.Channel;
import su.ternovskii.notificationservice.repository.NotificationTemplateRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Полный Spring-контекст с реальной PostgreSQL; Eureka отключена — в тестах не нужна
@Slf4j
@Testcontainers
@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
class NotificationTemplateOptimisticLockTest {

    // @Autowired на поле — способ инжекции в JUnit-тестах (@RequiredArgsConstructor здесь не работает)
    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // TransactionTemplate даёт явный контроль над границами транзакции прямо в коде теста
    @Autowired
    private TransactionTemplate transactionTemplate;

    // id шаблона из setUp — оба потока будут редактировать именно его
    private Long templateId;

    @BeforeEach
    void setUp() {
        notificationTemplateRepository.deleteAll();

        NotificationTemplateEntity template = new NotificationTemplateEntity();
        template.setChannel(Channel.EMAIL);
        template.setText("Начальный текст {message}");

        // execute() = BEGIN → лямбда → COMMIT; сохраняем до старта потоков
        NotificationTemplateEntity saved = transactionTemplate.execute(
                status -> notificationTemplateRepository.save(template)
        );

        templateId = saved.getId();
        log.info("[setUp] Шаблон создан: id={}, version={}", templateId, saved.getVersion());
    }

    @Test
    void updateText_whenTwoThreadsEditSameTemplate_oneShouldSucceedAndOneShouldFail() throws InterruptedException {

        // readLatch(2): основной поток ждёт, пока оба потока прочитают запись
        CountDownLatch readLatch = new CountDownLatch(2);
        // writeLatch(1): основной поток даёт сигнал обоим потокам начать запись одновременно
        CountDownLatch writeLatch = new CountDownLatch(1);

        // AtomicInteger нужен — обычный int++ не потокобезопасен при одновременном доступе
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // Одна задача запускается в двух потоках: читаем → ждём → пишем → фиксируем результат
        Runnable conflictingUpdate = () -> {
            String threadName = Thread.currentThread().getName();
            try {
                // Каждый поток запускает execute() независимо — у каждого своя транзакция
                transactionTemplate.execute(status -> {
                    NotificationTemplateEntity entity = notificationTemplateRepository
                            .findById(templateId)
                            .orElseThrow();

                    log.info("[{}] Прочитал шаблон: version={}", threadName, entity.getVersion());

                    // Сигнализируем: "я прочитал" — когда оба вызовут countDown(), счётчик станет 0
                    readLatch.countDown();

                    try {
                        // Ждём пока основной поток не даст старт; гарантируем оба видят version=0
                        writeLatch.await();
                    } catch (InterruptedException e) {
                        log.info(e.getMessage());
                        // Восстанавливаем флаг прерывания — нельзя "съедать" InterruptedException
                        Thread.currentThread().interrupt();
                    }

                    entity.setText("Текст от потока: " + threadName);

                    // saveAndFlush сразу шлёт UPDATE в БД (не ждёт коммита):
                    // UPDATE ... SET version=1 WHERE id=? AND version=0
                    // Второй поток после коммита первого увидит version=1 → 0 строк → исключение
                    log.info("[{}] Отправляет UPDATE с version={}", threadName, entity.getVersion());
                    return notificationTemplateRepository.saveAndFlush(entity);
                });

                log.info("[{}] Транзакция закоммичена успешно", threadName);
                successCount.incrementAndGet();

            } catch (ObjectOptimisticLockingFailureException e) {
                log.info(e.getMessage());
                // Hibernate обнаружил 0 обновлённых строк — версия уже изменена конкурентом
                log.warn("[{}] ObjectOptimisticLockingFailureException: версия устарела, обновление отклонено", threadName);
                failCount.incrementAndGet();
            }
        };

        // Пул из двух потоков — ровно для нашей гонки
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.submit(conflictingUpdate);
            executor.submit(conflictingUpdate);

            // Ждём пока оба потока прочитали — только тогда оба держат version=0
            readLatch.await();
            log.info("[main] Оба потока прочитали шаблон, даём старт на запись");

            // Открываем "стартовый пистолет" — оба потока одновременно идут в saveAndFlush
            writeLatch.countDown();

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }

        log.info("[main] Итог: успехов={}, провалов={}", successCount.get(), failCount.get());

        assertEquals(1, successCount.get(), "Ровно один поток должен успешно обновить запись");
        assertEquals(1, failCount.get(), "Ровно один поток должен получить OptimisticLockException");

        // Читаем финальное состояние из БД
        NotificationTemplateEntity finalState = transactionTemplate.execute(
                status -> notificationTemplateRepository.findById(templateId).orElseThrow()
        );

        log.info("[main] Финальное состояние: text='{}', version={}", finalState.getText(), finalState.getVersion());

        // После одного успешного обновления version должна быть ровно 1
        assertEquals(1L, finalState.getVersion(), "После одного успешного обновления version должна быть 1");
    }
}