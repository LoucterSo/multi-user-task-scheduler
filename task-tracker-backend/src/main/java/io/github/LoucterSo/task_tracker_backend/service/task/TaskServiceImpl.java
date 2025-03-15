package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.TaskNotFoundException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;

    @Override
    public TaskResponseForm saveTask(User user, TaskForm task) {

        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setDone(task.isDone());
        newTask.setUser(user);
        Task savedTask = taskRepository.save(newTask);
        return new TaskResponseForm(savedTask);
    }

    @Override
    public TaskResponseForm updateTask(TaskForm task, Long taskId) {
        Task taskToUpdate= taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setDone(task.isDone());
        Task savedTask = taskRepository.save(taskToUpdate);
        return new TaskResponseForm(savedTask);
    }

    @Override
    public TaskResponseForm deleteTaskById(Long taskId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));
        taskRepository.deleteById(taskId);

        return new TaskResponseForm(taskToDelete);
    }

    private Optional<Task> findTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public boolean userHasTask(User user, Long taskId) {
        Task task = findTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        return task.getUser().equals(user);
    }

    @Override
    public List<TaskResponseForm> getUserTasks(User user) {
        return  user.getTasks().stream()
                .map(TaskResponseForm::new)
                .toList();
    }

    @Override
    public TaskResponseForm getUserTaskById(User user, Long taskId) {
        if (!userHasTask(user, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to get this task");

        Task task = findTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        return new TaskResponseForm(task);
    }


}
