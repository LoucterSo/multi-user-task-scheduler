package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;

public interface AuthService {

    AuthResponseForm register(SignupForm signupForm, HttpServletResponse response);
    AuthResponseForm login(LoginForm loginForm, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    AuthResponseForm refreshToken(HttpServletRequest request);
}
