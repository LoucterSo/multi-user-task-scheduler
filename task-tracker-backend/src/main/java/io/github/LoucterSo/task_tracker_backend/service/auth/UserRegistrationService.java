package io.github.LoucterSo.task_tracker_backend.service.auth;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserAlreadyExists;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class UserRegistrationService {
    private final UserService userService;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(SignupForm signupForm) {
        User user = buildUserFromSignupForm(signupForm);
        Authority authority = createUserAuthority(user);
        try {
            userService.saveUser(user);
            authorityService.save(authority);
        } catch (DataIntegrityViolationException ex) {
            log.error("User passed login that already exists.");
            throw new UserAlreadyExists("User with email %s already exists".formatted(user.getEmail()));
        }
        return user;
    }

    private User buildUserFromSignupForm(SignupForm form) {
        return User.builder()
                .firstName(form.firstName())
                .lastName(form.lastName())
                .email(form.email().trim().toLowerCase())
                .password(passwordEncoder.encode(form.password()))
                .enabled(true)
                .build();
    }

    private Authority createUserAuthority(User user) {
        return Authority.builder()
                .role(Authority.Roles.USER)
                .user(user)
                .build();
    }
}