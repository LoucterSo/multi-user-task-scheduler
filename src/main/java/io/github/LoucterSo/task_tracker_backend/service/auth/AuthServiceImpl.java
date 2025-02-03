package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.form.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final JwtService jwtService;

    public ResponseEntity<AuthResponseForm> register(
            SignupForm signupForm,
            BindingResult validationResult,
            HttpServletResponse response) {

        if (validationResult.hasErrors())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponseForm.builder()
                            .message(validationResult.getFieldErrors().toString())
                            .build());

        final String email = signupForm.getEmail();

        if (userService.existsByEmail(email))
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(AuthResponseForm.builder()
                            .message("This email is already taken").build());

        final String password = passwordEncoder.encode(signupForm.getPassword());

        User user = User.builder()
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
                .email(email)
                .password(password)
                .authorities(new HashSet<>())
                .enabled(true)
                .build();

        Authority authority = authorityService.findByRole(Authority.Roles.USER).orElseThrow();

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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthResponseForm.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @Override
    public ResponseEntity<AuthResponseForm> login(
            LoginForm loginForm,
            BindingResult validationResult,
            HttpServletResponse response
    ) {

        if (validationResult.hasErrors())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponseForm.builder()
                            .message(validationResult.getFieldErrors().toString())
                            .build()); //ERROR

        final String email = loginForm.getEmail();
        final String password = loginForm.getPassword();

        Optional<User> user = userService.findByEmail(email); //ERROR

        if (user.isEmpty() || !passwordEncoder.matches(password, user.orElseThrow().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseForm.builder()
                            .message("User with wrong email or password")
                            .build()); //ERROR
        }

        String jwtAccess = jwtService.generateAccessToken(user.orElseThrow());
        String jwtRefresh = jwtService.generateRefreshToken(user.orElseThrow());

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtRefresh);
        refreshTokenCookie.setMaxAge((int) jwtService.getExpFromToken(jwtRefresh).getTime());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseForm.builder()
                        .message("Success")
                        .accessToken(jwtAccess)
                        .build());
    }

    @Override
    public ResponseEntity<AuthResponseForm> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseForm.builder()
                        .message("Success")
                        .build());
    }

    @Override
    public ResponseEntity<AuthResponseForm> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    final String refreshToken = cookie.getValue();

                    final String jwtAccess;
                    String email = jwtService.getSubjectFromToken(refreshToken)
                            .orElseThrow(); //Error

                    User user = userService.findByEmail(email)
                            .orElseThrow(); //Error

                    jwtAccess = jwtService.generateAccessToken(user);

                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(AuthResponseForm.builder()
                                    .accessToken(jwtAccess)
                                    .message("Success")
                                    .build());
                }
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponseForm.builder()
                        .message("There's no valid refresh token")
                        .build()); //ERROR
    }
}
