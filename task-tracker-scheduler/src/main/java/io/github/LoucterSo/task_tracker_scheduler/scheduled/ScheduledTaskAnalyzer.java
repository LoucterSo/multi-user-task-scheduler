package io.github.LoucterSo.task_tracker_scheduler.scheduled;

import io.github.LoucterSo.task_tracker_scheduler.form.email.EmailDto;
import io.github.LoucterSo.task_tracker_scheduler.form.user.UserWithTasksDto;
import io.github.LoucterSo.task_tracker_scheduler.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskAnalyzer {
    private final BackendService backendService;
    private final EmailService emailService;
    private final KafkaService kafkaService;

    @Value("${scheduler.email}")
    private String schedulerEmail;
    @Value("${scheduler.password}")
    private String schedulerPassword;

    @Scheduled(cron = "0 0 0 * * ?")
    public void analyzeAndSendTaskReports() {
        try {
            String accessToken = backendService.authenticate(schedulerEmail, schedulerPassword);
            List<UserWithTasksDto> usersWithTasks = backendService.getUsersWithTasks(accessToken);
            List<EmailDto> emailsToSend = emailService.analyzeTasks(usersWithTasks);
            emailsToSend.forEach(kafkaService::sendEmail);
        } catch (Exception e) {
            log.error("Error during task analysis and email sending. {}", e.getMessage());
        }
    }
}
