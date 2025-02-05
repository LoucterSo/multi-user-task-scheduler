package io.github.LoucterSo.task_tracker_backend.api.rest_controller;

import io.github.LoucterSo.task_tracker_backend.exception.ValidationFoundErrorsException;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);
    private final AuthService authService;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> register(
            @Valid @RequestBody SignupForm signupForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        if (validationResult.hasErrors()) {
            LOGGER.error("Invalid data send.");
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());
        }

        AuthResponseForm responseForm = authService.register(signupForm, response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseForm);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginForm loginForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        if (validationResult.hasErrors()) {
            LOGGER.error("Invalid data send.");
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());
        }

        AuthResponseForm responseForm = authService.login(loginForm, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseForm);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        AuthResponseForm responseForm = authService.logout(request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseForm);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        AuthResponseForm responseForm = authService.refreshToken(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseForm);
    }
}
