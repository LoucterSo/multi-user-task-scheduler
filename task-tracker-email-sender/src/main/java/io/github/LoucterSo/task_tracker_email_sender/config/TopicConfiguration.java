package io.github.LoucterSo.task_tracker_email_sender.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("EMAIL_SENDING_TASKS")
                .partitions(2)
                .build();
    }
}
