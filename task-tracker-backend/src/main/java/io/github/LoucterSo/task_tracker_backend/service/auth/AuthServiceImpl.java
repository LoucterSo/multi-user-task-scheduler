package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.TokenPair;
import io.github.LoucterSo.task_tracker_backend.service.authority.LoginValidator;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.kafka.KafkaService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class AuthServiceImpl implements AuthService {
    private final TokenService tokenService;
    private final LoginValidator loginValidator;
    private final UserRegistrationService registrationService;
    private final UserService userService;
    private final JwtService jwtService;
    private final KafkaService kafkaService;

    public AuthResponseForm register(
            SignupForm signupForm,
            HttpServletResponse response
    ) {
        User user = registrationService.registerUser(signupForm);
        log.info("User registered: {}", user.getEmail());
        TokenPair tokenPair = tokenService.createTokenPair(user);
        tokenService.createRefreshTokenCookie(response, tokenPair.refreshToken());
        kafkaService.sendWelcomeEmail(user.getEmail(), signupForm.firstName());
        log.info("Message sent to kafka.");
        return new AuthResponseForm(tokenPair.accessToken());
    }

    @Override
    public AuthResponseForm login(
            LoginForm loginForm,
            HttpServletResponse response
    ) {
        User user = loginValidator.validateLogin(
                loginForm.email().trim().toLowerCase(),
                loginForm.password()
        );
        log.info("User logged in: {}", user.getEmail());
        TokenPair tokenPair = tokenService.createTokenPair(user);
        tokenService.createRefreshTokenCookie(response, tokenPair.refreshToken());
        return new AuthResponseForm(tokenPair.accessToken());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        tokenService.deleteRefreshTokenCookie(response);
        log.info("User logged out");
    }

    @Override
    public AuthResponseForm refreshToken(HttpServletRequest request) {
        String refreshToken = tokenService.extractRefreshToken(request);
        String email = jwtService.verifyToken(refreshToken);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} doesn't exist", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });
        String newAccessToken = jwtService.generateAccessToken(user);
        log.info("New access toke generated.");
        return new AuthResponseForm(newAccessToken);
    }
}
