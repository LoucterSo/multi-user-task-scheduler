package io.github.LoucterSo.task_tracker_backend.service.authority;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class LoginValidator {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public User validateLogin(String email, String password) {
        User user = userService.findByEmail(email).orElseThrow(
                () -> {
                    log.error("User passed email that doesn't exist. Email: {}.", email);
                    return new AuthenticationFailedException("User with email %s doesn't exist".formatted(email));
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("User passed wrong password.");
            throw new AuthenticationFailedException("Wrong password %s".formatted(password));
        }

        return user;
    }
}