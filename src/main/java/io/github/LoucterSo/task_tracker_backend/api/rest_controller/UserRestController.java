package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.UserResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<UserResponseForm> getCurrentUserData(@AuthenticationPrincipal User currentUser) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserResponseForm.builder()
                        .message("Success")
                        .id(currentUser.getId())
                        .email(currentUser.getEmail())
                        .build());
    }

    @GetMapping
    public List<User> getAllUsers() {

        return userService.findAll();
    }

}
