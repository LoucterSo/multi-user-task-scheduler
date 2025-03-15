package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import java.util.List;

public interface TaskService {
    TaskResponseForm saveTask(User user, TaskForm task);
    TaskResponseForm deleteTaskById(Long taskId);
    boolean userHasTask(User user, Long taskId);
    TaskResponseForm updateTask(TaskForm taskForm, Long taskId);
    List<TaskResponseForm> getUserTasks(User user);
    TaskResponseForm getUserTaskById(User user, Long taskId);
}
