package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    @Transactional
    public ResponseEntity<List<?>> getUserTask(@AuthenticationPrincipal User currentUser) {

        User userWithTasks = userService.findById(currentUser.getId()).orElseThrow();

        List<TaskResponseForm> userTasks = userWithTasks.getTasks().stream()
                .map(TaskResponseForm::new)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userTasks);
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Task task
    ) {

        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setUser(currentUser);

        TaskResponseForm createdTask = taskService.saveTask(newTask);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);

    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task taskToUpdate,
            @AuthenticationPrincipal User currentUser
    ) {

        Task task = taskService.findTaskById(taskId).orElseThrow();

        if (!task.getUser().equals(currentUser)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User doesn't have rights to change this task");
        }

        task.setTitle(taskToUpdate.getTitle());
        task.setDescription(taskToUpdate.getDescription());
        task.setDone(taskToUpdate.isDone());

        TaskResponseForm mergedTask = taskService.saveTask(task);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mergedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        Task task = taskService.findTaskById(taskId).orElseThrow();

        if (!task.getUser().equals(currentUser)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User doesn't have rights to change this task");
        }

        TaskResponseForm removedTask = taskService.deleteTaskById(taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removedTask);
    }
}
