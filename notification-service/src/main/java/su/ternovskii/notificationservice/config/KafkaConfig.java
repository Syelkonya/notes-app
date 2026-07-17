package su.ternovskii.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic smsSendTopic() {
        return TopicBuilder.name("notification.sms.send")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pushSendTopic() {
        return TopicBuilder.name("notification.push.send")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailSendTopic() {
        return TopicBuilder.name("notification.email.send")
                .partitions(1)
                .replicas(1)
                .build();
    }
}