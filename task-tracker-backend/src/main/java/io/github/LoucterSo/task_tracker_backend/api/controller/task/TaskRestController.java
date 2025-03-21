package io.github.LoucterSo.task_tracker_backend.api.controller.task;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.UserNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.ValidationFoundErrorsException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRestController.class);
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<TaskResponseForm>> getUserTasks(@AuthenticationPrincipal User currentUser) {

        User userWithTasks = userService.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        List<TaskResponseForm> userTasks = taskService.getUserTasks(userWithTasks);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userTasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseForm> getUserTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        TaskResponseForm taskResponseForm = taskService.getUserTaskById(currentUser, taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskResponseForm);
    }

    @PostMapping
    public ResponseEntity<TaskResponseForm> createTask(
            @Valid @RequestBody TaskForm task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        TaskResponseForm createdTask = taskService.saveTask(currentUser, task);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);

    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseForm> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskForm task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        if (!taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to change this task");

        TaskResponseForm mergedTask = taskService.updateTask(task, taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mergedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskResponseForm> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        if (!taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to delete this task");

        TaskResponseForm removedTask = taskService.deleteTaskById(taskId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removedTask);
    }
}
