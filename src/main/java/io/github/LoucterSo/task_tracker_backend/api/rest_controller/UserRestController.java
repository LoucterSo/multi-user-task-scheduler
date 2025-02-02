package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.UserResponseForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    @GetMapping("/current")
    public ResponseEntity<UserResponseForm> getCurrentUserData() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Objects.requireNonNull(authentication);
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserResponseForm.builder()
                        .message("Success")
                        .id(currentUser.getId())
                        .email(currentUser.getEmail())
                        .build());
    }

}
