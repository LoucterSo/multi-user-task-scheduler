package io.github.LoucterSo.task_tracker_email_sender.consumer;

import io.github.LoucterSo.task_tracker_email_sender.form.EmailDto;
import io.github.LoucterSo.task_tracker_email_sender.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor @Slf4j
public class KafkaEmailConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "EMAIL_SENDING_TASKS")
    public void sendEmail(EmailDto emailDto) {
        mailService.sendEmail(emailDto.to(), emailDto.header(), emailDto.text());
    }
}
