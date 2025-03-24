package io.github.LoucterSo.task_tracker_scheduler.service;

import io.github.LoucterSo.task_tracker_scheduler.form.email.EmailDto;
import io.github.LoucterSo.task_tracker_scheduler.form.task.TaskDto;
import io.github.LoucterSo.task_tracker_scheduler.form.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final String SUBJECT_FOR_DONE_TASKS_TITLE = "You've done %d tasks today!";
    private static final String SUBJECT_FOR_NOT_DONE_TASKS = "You've got %d unfinished tasks!";
    private static final String TEXT_FOR_DONE_TASKS_TITLE = "Good night, %s %s!%nToday you've done %d tasks:%n%s";
    private static final String TEXT_FOR_NOT_DONE_TASKS = "Good night, %s %s!%nYou've got %d unfinished tasks:%n%s";

    public List<EmailDto> analyzeTasks(List<UserDto> users) {
        List<EmailDto> emailsToSend = new ArrayList<>();

        for (UserDto user : users) {
            Set<TaskDto> tasks = user.tasks();
            if (tasks.isEmpty()) continue;

            Timestamp beginningOfTheDay = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
            List<TaskDto> doneTasksForDay = tasks.stream()
                    .filter(TaskDto::isDone)
                    .filter(t -> t.completionTime().after(beginningOfTheDay))
                    .sorted(Comparator.comparing(TaskDto::completionTime))
                    .toList();

            List<TaskDto> notDoneTasks = tasks.stream()
                    .filter(t -> !t.isDone())
                    .toList();

            long quantityOfDoneTasksForToday = doneTasksForDay.size();
            long quantityOfNotDoneTasks = notDoneTasks.size();

            if (quantityOfDoneTasksForToday > 0) {
                emailsToSend.add(createEmail(user, doneTasksForDay, SUBJECT_FOR_DONE_TASKS_TITLE, TEXT_FOR_DONE_TASKS_TITLE));
            }
            if (quantityOfNotDoneTasks > 0) {
                emailsToSend.add(createEmail(user, notDoneTasks, SUBJECT_FOR_NOT_DONE_TASKS, TEXT_FOR_NOT_DONE_TASKS));
            }
        }

        return emailsToSend;
    }

    private EmailDto createEmail(UserDto user, List<TaskDto> tasks, String subjectTemplate, String textTemplate) {
        String header = subjectTemplate.formatted(tasks.size());
        String tasksToDisplay = createTasksToDisplay(tasks);
        String text = textTemplate.formatted(user.firstName(), user.lastName(), tasks.size(), tasksToDisplay);
        return new EmailDto(user.email(), header, text);
    }

    private String createTasksToDisplay(List<TaskDto> tasks) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            if (i == 5) break;

            boolean isLastTask = i == tasks.size() - 1;
            TaskDto taskDto = tasks.get(i);
            stringBuilder.append(i + 1).append(". ").append(taskDto.title())
                    .append(": ").append(taskDto.description()).append(isLastTask ? "" : "\r\n");

        }

        return stringBuilder.toString();
    }
}
