package io.github.LoucterSo.task_tracker_backend.api.controller.user;

import io.github.LoucterSo.task_tracker_backend.exception.ValidationFoundErrorsException;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor @Slf4j
public class AuthRestController {
    private final AuthService authService;

    @PostMapping(value = "/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AuthResponseForm register(
            @Valid @RequestBody SignupForm signupForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        return authService.register(signupForm, response, validationResult);
    }

    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AuthResponseForm login(
            @Valid @RequestBody LoginForm loginForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        return authService.login(loginForm, response, validationResult);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AuthResponseForm refreshToken(HttpServletRequest request) {

        return authService.refreshToken(request);
    }
}
