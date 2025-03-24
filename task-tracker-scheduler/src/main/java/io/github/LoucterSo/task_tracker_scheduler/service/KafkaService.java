package io.github.LoucterSo.task_tracker_scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_scheduler.form.email.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEmail(EmailDto email) {
        try {
            kafkaTemplate.send("EMAIL_SENDING_TASKS", objectMapper.writeValueAsString(email));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send email to Kafka", e);
        }
    }
}
