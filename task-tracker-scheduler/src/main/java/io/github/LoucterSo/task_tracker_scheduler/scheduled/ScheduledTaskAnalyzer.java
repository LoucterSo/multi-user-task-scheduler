package io.github.LoucterSo.task_tracker_scheduler.scheduled;

import io.github.LoucterSo.task_tracker_scheduler.form.email.EmailDto;
import io.github.LoucterSo.task_tracker_scheduler.form.user.UserDto;
import io.github.LoucterSo.task_tracker_scheduler.service.AuthService;
import io.github.LoucterSo.task_tracker_scheduler.service.EmailService;
import io.github.LoucterSo.task_tracker_scheduler.service.KafkaService;
import io.github.LoucterSo.task_tracker_scheduler.service.UserTaskService;
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
    private final AuthService authService;
    private final UserTaskService userTaskService;
    private final EmailService emailService;
    private final KafkaService kafkaService;

    @Value("${scheduler.email}")
    private String schedulerEmail;
    @Value("${scheduler.password}")
    private String schedulerPassword;

    @Scheduled(cron = "0 0 0 * * ?")
    public void analyzeAndSendTaskReports() {
        try {
            String accessToken = authService.authenticate(schedulerEmail, schedulerPassword);
            List<UserDto> usersWithTasks = userTaskService.getUsersWithTasks(accessToken);
            List<EmailDto> emailsToSend = emailService.analyzeTasks(usersWithTasks);
            emailsToSend.forEach(kafkaService::sendEmail);
        } catch (Exception e) {
            log.error("Error during task analysis and email sending", e);
        }
    }
}
