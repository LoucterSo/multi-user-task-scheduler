package io.github.LoucterSo.task_tracker_backend.api.controller.task;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.ValidationFoundErrorsException;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor @Slf4j
public class TaskRestController {
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TaskDto> getUserTasks(@AuthenticationPrincipal User currentUser) {

        User userWithTasks = userService.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        return taskService.getUserTasks(userWithTasks);
    }

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskDto getUserTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        return taskService.getUserTaskById(currentUser, taskId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TaskDto createTask(
            @Valid @RequestBody TaskDto task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        return taskService.saveTask(currentUser, task);

    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskDto updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDto task,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        if (!taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to change this task");

        return taskService.updateTask(task, taskId);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskDto deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) {

        if (!taskService.userHasTask(currentUser, taskId))
            throw new AuthenticationFailedException("User doesn't have rights to delete this task");

        return taskService.deleteTaskById(taskId);
    }
}
