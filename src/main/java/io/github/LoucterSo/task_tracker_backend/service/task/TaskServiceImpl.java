package io.github.LoucterSo.task_tracker_backend.service.task;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.form.TaskResponseForm;
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
    public TaskResponseForm saveTask(Task task) {

        Task savedTask = taskRepository.save(task);
        return new TaskResponseForm(savedTask);
    }

    @Override
    @Transactional
    public TaskResponseForm deleteTaskById(Long taskId) {
        Task taskToDelete = taskRepository.findById(taskId).orElseThrow();
        taskRepository.deleteById(taskId);

        return new TaskResponseForm(taskToDelete);
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

}
