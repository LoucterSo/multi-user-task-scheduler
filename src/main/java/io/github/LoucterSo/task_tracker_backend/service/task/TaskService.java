package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.form.TaskResponseForm;
import java.util.Optional;

public interface TaskService {
    TaskResponseForm saveTask(Task task);
    TaskResponseForm deleteTaskById(Long taskId);
    Optional<Task> findTaskById(Long taskId);
}
