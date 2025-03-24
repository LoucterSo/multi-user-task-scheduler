package io.github.LoucterSo.task_tracker_backend.api.controller.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserNotFoundException;
import io.github.LoucterSo.task_tracker_backend.form.user.UserDto;
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
    public UserDto getCurrentUserData(@AuthenticationPrincipal User currentUser) {

        return new UserDto(currentUser.getId(), currentUser.getEmail(), currentUser.getFirstName(), currentUser.getLastName(), null);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserDto> getAllUsersWithTasks() {

        return userService.getAllUsersWithTasks();
    }

}
