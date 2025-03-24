package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.task.TaskNotFoundException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;
import io.github.LoucterSo.task_tracker_backend.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor @Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public TaskDto saveTask(User user, TaskDto task) {
        Task newTask = new Task();
        newTask.setTitle(task.title());
        newTask.setDescription(task.description());
        newTask.setDone(task.isDone());
        newTask.setUser(user);
        Task savedTask = taskRepository.save(newTask);

        return TaskDto.fromEntity(savedTask);
    }

    @Override
    public TaskDto updateTask(TaskDto task, Long taskId) {
        Task taskToUpdate= taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        taskToUpdate.setTitle(task.title());
        taskToUpdate.setDescription(task.description());
        taskToUpdate.setDone(task.isDone());
        Task savedTask = taskRepository.save(taskToUpdate);

        return TaskDto.fromEntity(savedTask);
    }

    @Override
    public TaskDto deleteTaskById(Long taskId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));
        taskRepository.deleteById(taskId);

        return TaskDto.fromEntity(taskToDelete);
    }

    @Override
    public boolean userHasTask(User user, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        return task.getUser().equals(user);
    }

    @Override
    public List<TaskDto> getUserTasks(User user) {
        return  user.getTasks().stream()
                .map(TaskDto::fromEntity)
                .toList();
    }

    @Override
    public TaskDto getUserTaskById(User user, Long taskId) {
        if (!userHasTask(user, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to get this task");

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        return TaskDto.fromEntity(task);
    }


}
