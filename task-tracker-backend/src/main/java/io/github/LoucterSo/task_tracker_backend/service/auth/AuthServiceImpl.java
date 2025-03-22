package io.github.LoucterSo.task_tracker_backend.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.form.email.EmailDto;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final JwtService jwtService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AuthResponseForm register(
            SignupForm signupForm,
            HttpServletResponse response,
            BindingResult validationResult) {

        LOGGER.info("Starting processing the registration method.");

        if (validationResult.hasErrors()) {
            LOGGER.error("Invalid data send.");
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());
        }

        final String email = signupForm.getEmail().trim().toLowerCase();

        final String password = passwordEncoder.encode(signupForm.getPassword());

        User user = User.builder()
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
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
            LOGGER.error("User passed login that already exists.");
            throw new UserAlreadyExists("User with email %s already exists".formatted(email));
        }

        LOGGER.info("New user created. Email: {}.", user.getEmail());

        String[] tokens = createTokenPair(user);
        String refreshToken = tokens[1];
        String accessToken = tokens[0];

        createRefreshTokenCookie(response, refreshToken);

        try {
            kafkaTemplate.send("EMAIL_SENDING_TASKS", "" + new Random().nextInt(0, 2), objectMapper.writeValueAsString(new EmailDto(email, "Welcome!", "Hi!"))); //!!!!
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Message sent to kafka");

        LOGGER.info("Stopping processing the registration method.");

        return AuthResponseForm.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public AuthResponseForm login(
            LoginForm loginForm,
            HttpServletResponse response,
            BindingResult validationResult
    ) {

        LOGGER.info("Starting processing the login method.");

        if (validationResult.hasErrors()) {
            LOGGER.error("Invalid data send.");
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());
        }

        final String email = loginForm.getEmail().trim().toLowerCase();
        final String password = loginForm.getPassword();

        User user = userService.findByEmail(email).orElseThrow(
                () -> {
                    LOGGER.error("User passed email that doesn't exist. Email: {}.", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            LOGGER.error("User passed wrong password.");
            throw new AuthenticationFailedException("Wrong password %s".formatted(password));
        }

        LOGGER.info("User with passed data found.");

        String[] tokens = createTokenPair(user);
        String refreshToken = tokens[1];
        String accessToken = tokens[0];

        createRefreshTokenCookie(response, refreshToken);

        LOGGER.info("Stopping processing the login method.");

        return AuthResponseForm.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public AuthResponseForm logout(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("Starting processing the logout method.");

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
            LOGGER.info("Refresh token cookie deleted.");
        }

        LOGGER.info("User is logged out.");

        LOGGER.info("Stopping processing the logout method.");

        return AuthResponseForm.builder()
                .build();
    }

    @Override
    public AuthResponseForm refreshToken(HttpServletRequest request) {

        LOGGER.info("Starting processing the refresh token method.");

        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> {
                    LOGGER.error("Cookies not found.");
                    return new RefreshTokenNotFoundException("Refresh token cookie not found");
                });

        Cookie refreshCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findAny()
                .orElseThrow(() -> {
                    LOGGER.error("Refresh token cookie not found.");
                    return new RefreshTokenNotFoundException("Refresh token cookie not found");
                });

        final String refreshToken = refreshCookie.getValue();

        LOGGER.info("Refresh token found. Verifying token...");

        final String jwtAccess;
        String email = jwtService.getSubjectFromToken(refreshToken)
                .orElseThrow(() -> {
                    LOGGER.error("Token doesn't have subject");
                    return new MalformedJwtException("Token doesn't have subject");
                });

        User user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.error("User with email {} doesn't exist", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });

        LOGGER.info("Refresh token is valid.");

        jwtAccess = jwtService.generateAccessToken(user);

        LOGGER.info("New access toke generated.");

        LOGGER.info("Stopping processing the refresh token method.");

        return AuthResponseForm.builder()
                .accessToken(jwtAccess)
                .build();
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String jwtRefresh) {

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtRefresh);
        refreshTokenCookie.setMaxAge((int) jwtService.getExpFromToken(jwtRefresh).getTime());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        LOGGER.info("Refresh token passed to cookies.");
    }

    private String[] createTokenPair(User user) {
        String jwtAccess = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);

        LOGGER.info("Access and refresh tokens created.");

        return new String[]{jwtAccess, jwtRefresh};
    }

}
