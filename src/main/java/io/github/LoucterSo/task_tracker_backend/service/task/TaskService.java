package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    TaskResponseForm saveTask(TaskForm task, User user);
    TaskResponseForm deleteTaskById(Long taskId);
    Optional<Task> findTaskById(Long taskId);
    boolean userHasTask(User user, Long taskId);
    TaskResponseForm updateTask(TaskForm taskForm, Long taskId);
    List<TaskResponseForm> getUserTasks(User user);
}
