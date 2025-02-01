package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.AuthResponseData;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/user")
    public ResponseEntity<AuthResponseData> signup(@RequestBody SignupForm signupForm) {
        final String email = signupForm.email();

        if (userService.existsByEmail(email))
            return  ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(AuthResponseData.builder()
                            .message("This email is already taken").build());

        final String password = passwordEncoder.encode(signupForm.password());

        User user = User.builder()
                .firstName(signupForm.firstName())
                .lastName(signupForm.lastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();

        Authority authority = new Authority();
        authority.setRole(Authority.Roles.USER);

        user.addRole(authority);

        userService.saveUser(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseData.builder()
                        .message("Success")
                        .build());
    }

}
