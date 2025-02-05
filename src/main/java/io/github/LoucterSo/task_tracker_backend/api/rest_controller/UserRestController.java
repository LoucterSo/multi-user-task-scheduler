package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtServiceImpl;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);
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
    public ResponseEntity<List<UserResponseForm>> getAllUsers() {

        List<UserResponseForm> responseForms = userService.findAll().stream()
                .map(user -> UserResponseForm.builder()
                        .message("Success")
                        .id(user.getId())
                        .email(user.getEmail())
                        .build()).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseForms);
    }

}
