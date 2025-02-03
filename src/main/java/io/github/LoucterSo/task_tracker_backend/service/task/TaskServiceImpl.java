package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.exception.TaskNotFoundException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskResponseForm saveTask(TaskForm task, User user) {

        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setDone(task.isDone());
        newTask.setUser(user);
        Task savedTask = taskRepository.save(newTask);
        return new TaskResponseForm(savedTask);
    }

    @Override
    @Transactional
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
    @Transactional
    public TaskResponseForm deleteTaskById(Long taskId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));
        taskRepository.deleteById(taskId);

        return new TaskResponseForm(taskToDelete);
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public boolean userHasTask(User user, Long taskId) {
        Task task = findTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(taskId)));

        return !task.getUser().equals(user);
    }

    @Override
    public List<TaskResponseForm> getUserTasks(User user) {
        return  user.getTasks().stream()
                .map(TaskResponseForm::new)
                .toList();
    }


}
