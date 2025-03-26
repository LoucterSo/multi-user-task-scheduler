package io.github.LoucterSo.task_tracker_backend.api.controller.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithTasksDto;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithoutTasks;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService userService;

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserWithoutTasks getCurrentUserData(@AuthenticationPrincipal User currentUser) {

        return UserWithoutTasks.fromEntity(currentUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserWithTasksDto> getAllUsersWithTasks() {

        return userService.getAllUsersWithTasks();
    }

}
