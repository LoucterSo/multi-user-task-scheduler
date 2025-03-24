package io.github.LoucterSo.task_tracker_backend.config;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.repository.user.AuthorityRepository;
import io.github.LoucterSo.task_tracker_backend.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class Initialization {

    @Value("${scheduler.email}")
    private String schedulerEmail;
    @Value("${scheduler.password}")
    private String schedulerPassword;

    @Bean
    @Transactional
    public CommandLineRunner commandLineRunner(
            UserRepository userRepository,
            AuthorityRepository authRepository,
            PasswordEncoder encoder
    ) {

        return runner -> {

            try {
                User scheduler = User.builder()
                        .firstName("admin")
                        .lastName("scheduler")
                        .email(schedulerEmail)
                        .password(encoder.encode(schedulerPassword))
                        .enabled(true)
                        .build();

                Authority userRole = Authority.builder().role(Authority.Roles.USER).user(scheduler).build();
                Authority adminRole = Authority.builder().role(Authority.Roles.ADMIN).user(scheduler).build();
                scheduler.setAuthorities(Set.of(userRole, adminRole));

                userRepository.save(scheduler);
                authRepository.save(userRole);
                authRepository.save(adminRole);

                schedulerEmail = null;
                schedulerPassword = null;
            } catch (DataIntegrityViolationException ex) {
            }
        };
    }
}
