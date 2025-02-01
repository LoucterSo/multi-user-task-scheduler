package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.AuthResponseData;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping(value = "/user")
    public ResponseEntity<AuthResponseData> signup(@RequestBody SignupForm signupForm) {
        final String email = signupForm.getEmail();

        if (userService.existsByEmail(email))
            return  ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(AuthResponseData.builder()
                            .message("This email is already taken").build());

        final String password = passwordEncoder.encode(signupForm.getPassword());

        User user = User.builder()
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();

        Authority authority = new Authority();
        authority.setRole(Authority.Roles.USER);

        user.addRole(authority);

        userService.saveUser(user);

        String jwtAccess = jwtService.generateAccessToken(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseData.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @PostMapping(value = "/auth/login")
    public ResponseEntity<AuthResponseData> login(@RequestBody SignupForm signupForm) {
        final String email = signupForm.getEmail();
        final String password = signupForm.getPassword();

        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseData.builder()
                            .message("User with wrong email or password")
                            .build());
        }

        String jwtAccess = jwtService.generateAccessToken(user.get());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseData.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @GetMapping("/user")
    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Objects.requireNonNull(authentication);

        return (User) authentication.getPrincipal();
    }

}
