package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import io.jsonwebtoken.MalformedJwtException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final JwtService jwtService;

    public AuthResponseForm register(
            SignupForm signupForm,
            HttpServletResponse response) {

        LOGGER.info("Starting processing the registration method.");

        final String email = signupForm.getEmail();

        if (userService.existsByEmail(email)) {
            LOGGER.error("User passed login that already exists.");
            throw new UserAlreadyExists("User with email %s already exists".formatted(email));
        }

        final String password = passwordEncoder.encode(signupForm.getPassword());

        User user = User.builder()
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();

        Authority authority = authorityService.findByRole(Authority.Roles.USER)
                .orElseThrow(() -> new UnexpectedServerException("There's no USER role"));

        user.addRole(authority);

        userService.saveUser(user);

        LOGGER.info("New user created. Email: {}.", user.getEmail());

        String[] tokens = createTokenPair(user);

        createRefreshTokenCookie(response, tokens[1]);

        LOGGER.info("Stopping processing the registration method.");

        return AuthResponseForm.builder()
                .accessToken(tokens[0])
                .build();
    }

    @Override
    public AuthResponseForm login(
            LoginForm loginForm,
            HttpServletResponse response
    ) {

        LOGGER.info("Starting processing the login method.");

        final String email = loginForm.getEmail();
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

        createRefreshTokenCookie(response, tokens[1]);

        LOGGER.info("Stopping processing the login method.");

        return AuthResponseForm.builder()
                .accessToken(tokens[0])
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
