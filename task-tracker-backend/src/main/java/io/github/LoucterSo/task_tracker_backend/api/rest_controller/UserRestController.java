package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;
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

        UserResponseForm userResponseForm = userService.createUserResponseForm(currentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponseForm);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseForm>> getAllUserData() {

        List<UserResponseForm> responseForms = userService.getAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseForms);
    }

}
