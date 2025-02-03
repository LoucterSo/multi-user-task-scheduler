package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.form.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface AuthService {

    ResponseEntity<AuthResponseForm> register(SignupForm signupForm, BindingResult validationResult, HttpServletResponse response);
    ResponseEntity<AuthResponseForm> login(LoginForm loginForm, BindingResult validationResult, HttpServletResponse response);
    ResponseEntity<AuthResponseForm> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<AuthResponseForm> refreshToken(HttpServletRequest request);
}
