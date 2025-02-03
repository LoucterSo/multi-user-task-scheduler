package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Task;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.exception.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.UnexpectedServerException;
import io.github.LoucterSo.task_tracker_backend.exception.ValidationFoundErrorsException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
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

        User userWithTasks = userService.findById(currentUser.getId())
                .orElseThrow(() -> new UnexpectedServerException("Current user not found"));

        List<TaskResponseForm> userTasks = taskService.getUserTasks(userWithTasks);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userTasks);
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @Valid @RequestBody TaskForm task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        TaskResponseForm createdTask = taskService.saveTask(task, currentUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);

    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskForm task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        if (taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to change this task");

        TaskResponseForm mergedTask = taskService.updateTask(task, taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mergedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        if (taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to change this task");

        TaskResponseForm removedTask = taskService.deleteTaskById(taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removedTask);
    }
}
