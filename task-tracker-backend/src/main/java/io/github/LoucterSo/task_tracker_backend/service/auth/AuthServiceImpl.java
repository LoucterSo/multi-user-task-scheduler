package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.auth.RefreshTokenNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserAlreadyExists;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.kafka.KafkaService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final JwtService jwtService;
    private final KafkaService kafkaService;

    public AuthResponseForm register(
            SignupForm signupForm,
            HttpServletResponse response
    ) {
        log.info("Starting processing the registration method.");

        final String email = signupForm.email().trim().toLowerCase();
        final String password = passwordEncoder.encode(signupForm.password());

        User user = User.builder()
                .firstName(signupForm.firstName())
                .lastName(signupForm.lastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();
        Authority authority = Authority.builder().role(Authority.Roles.USER).user(user).build();
        user.addRole(authority);

        try {
            userService.saveUser(user);
            authorityService.save(authority);
        } catch (DataIntegrityViolationException ex) {
            log.error("User passed login that already exists.");
            throw new UserAlreadyExists("User with email %s already exists".formatted(email));
        }

        log.info("New user created. Email: {}.", user.getEmail());
        String[] tokens = createTokenPair(user);
        String refreshToken = tokens[1];
        String accessToken = tokens[0];
        createRefreshTokenCookie(response, refreshToken);

        kafkaService.sendWelcomeEmail(email, signupForm.firstName().trim());
        log.info("Message sent to kafka.");

        log.info("Stopping processing the registration method.");
        return new AuthResponseForm(accessToken);
    }

    @Override
    public AuthResponseForm login(
            LoginForm loginForm,
            HttpServletResponse response
    ) {
        log.info("Starting processing the login method.");

        final String email = loginForm.email().trim().toLowerCase();
        final String password = loginForm.password();

        User user = userService.findByEmail(email).orElseThrow(
                () -> {
                    log.error("User passed email that doesn't exist. Email: {}.", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("User passed wrong password.");
            throw new AuthenticationFailedException("Wrong password %s".formatted(password));
        }

        log.info("User with passed data found.");
        String[] tokens = createTokenPair(user);
        String refreshToken = tokens[1];
        String accessToken = tokens[0];
        createRefreshTokenCookie(response, refreshToken);

        log.info("Stopping processing the login method.");
        return new AuthResponseForm(accessToken);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Starting processing the logout method.");
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(c -> c.getName().equals("refreshToken"))
                    .findAny()
                    .ifPresent(cookie -> {
                        cookie.setValue("");
                        cookie.setMaxAge(0);
                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    });
            log.info("Refresh token cookie deleted.");
        }

        log.info("User is logged out.");
        log.info("Stopping processing the logout method.");
    }

    @Override
    public AuthResponseForm refreshToken(HttpServletRequest request) {
        log.info("Starting processing the refresh token method.");

        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> {
                    log.error("Cookies not found.");
                    return new RefreshTokenNotFoundException("Refresh token cookie not found");
                });

        Cookie refreshCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findAny()
                .orElseThrow(() -> {
                    log.error("Refresh token cookie not found.");
                    return new RefreshTokenNotFoundException("Refresh token cookie not found");
                });

        final String refreshToken = refreshCookie.getValue();
        log.info("Refresh token found. Verifying token...");
        final String jwtAccess;

        String email = jwtService.getSubjectFromToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("Token doesn't have subject");
                    return new MalformedJwtException("Token doesn't have subject");
                });

        User user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} doesn't exist", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });

        log.info("Refresh token is valid.");
        jwtAccess = jwtService.generateAccessToken(user);
        log.info("New access toke generated.");
        log.info("Stopping processing the refresh token method.");
        return new AuthResponseForm(jwtAccess);
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String jwtRefresh) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtRefresh);
        refreshTokenCookie.setMaxAge((int) jwtService.getExpFromToken(jwtRefresh).getTime());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);
        log.info("Refresh token passed to cookies.");
    }

    private String[] createTokenPair(User user) {
        String jwtAccess = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);
        log.info("Access and refresh tokens created.");
        return new String[]{jwtAccess, jwtRefresh};
    }

}
