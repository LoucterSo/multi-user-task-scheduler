package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;
import java.util.List;

public interface TaskService {
    TaskDto saveTask(User user, TaskDto task);
    TaskDto deleteTaskById(Long taskId);
    boolean userHasTask(User user, Long taskId);
    TaskDto updateTask(TaskDto taskForm, Long taskId);
    List<TaskDto> getUserTasks(User user);
    TaskDto getUserTaskById(User user, Long taskId);
}
