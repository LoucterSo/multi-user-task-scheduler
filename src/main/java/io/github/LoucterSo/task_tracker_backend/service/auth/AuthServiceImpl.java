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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final JwtService jwtService;

    public AuthResponseForm register(
            SignupForm signupForm,
            BindingResult validationResult,
            HttpServletResponse response) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        final String email = signupForm.getEmail();

        if (userService.existsByEmail(email))
            throw new UserAlreadyExists("User with email %s already exists".formatted(email));

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

        String jwtAccess = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtRefresh);
        refreshTokenCookie.setMaxAge((int) jwtService.getExpFromToken(jwtRefresh).getTime());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return AuthResponseForm.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build();
    }

    @Override
    public AuthResponseForm login(
            LoginForm loginForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        if (validationResult.hasErrors())
            throw new ValidationFoundErrorsException(validationResult.getFieldErrors());

        final String email = loginForm.getEmail();
        final String password = loginForm.getPassword();

        User user = userService.findByEmail(email).orElseThrow(
                () -> new AuthenticationFailedException("User with email %s doesn't exist".formatted(email)));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new AuthenticationFailedException("Wrong password %s".formatted(password));

        String jwtAccess = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtRefresh);
        refreshTokenCookie.setMaxAge((int) jwtService.getExpFromToken(jwtRefresh).getTime());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return AuthResponseForm.builder()
                        .message("Authenticated")
                        .accessToken(jwtAccess)
                        .build();
    }

    @Override
    public AuthResponseForm logout(HttpServletRequest request, HttpServletResponse response) {
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
        }

        return AuthResponseForm.builder()
                        .message("User logout")
                        .build();
    }

    @Override
    public AuthResponseForm refreshToken(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() ->  new RefreshTokenNotFoundException("Refresh token cookie not found"));

        Cookie refreshCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findAny()
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token cookie not found"));

        final String refreshToken = refreshCookie.getValue();

        final String jwtAccess;
        String email = jwtService.getSubjectFromToken(refreshToken)
                .orElseThrow(() -> new MalformedJwtException("Token doesn't have subject"));

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException("User with email %s doesn't exist".formatted(email)));

        jwtAccess = jwtService.generateAccessToken(user);

        return AuthResponseForm.builder()
                        .accessToken(jwtAccess)
                        .message("Success")
                        .build();
    }

}
