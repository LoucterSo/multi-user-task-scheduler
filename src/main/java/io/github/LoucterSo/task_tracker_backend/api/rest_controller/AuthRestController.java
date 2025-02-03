package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.form.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupForm signupForm,
            HttpServletResponse response,
            BindingResult validationResult
    ) {

        return authService.register(signupForm, validationResult, response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginForm loginForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        return authService.login(loginForm, validationResult, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        return authService.logout(request, response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        return authService.refreshToken(request);
    }
}
