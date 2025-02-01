package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.AuthResponseData;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedirectStrategy redirectStrategy;
    private final AuthenticationProvider authenticationProvider;

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

        String jwtAccess = jwtService.generateAccessToken(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseData.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @GetMapping("/private")
    public String privateS() {

        return "private";
    }

}
