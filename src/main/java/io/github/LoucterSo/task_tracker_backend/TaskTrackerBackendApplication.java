package io.github.LoucterSo.task_tracker_backend;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import io.github.LoucterSo.task_tracker_backend.service.AuthorityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@SpringBootApplication
public class TaskTrackerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            UserRepository userRepository,
            AuthorityService authorityService,
            PasswordEncoder encoder
    ) {

        return runner -> {

            for (Authority.Roles role : Authority.Roles.values()) {
                authorityService.findByRole(role).orElseGet(() -> {
                    Authority authority = new Authority(role);
                    authorityService.save(authority);
                    return authority;
                });
            }

            User admin = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("loucterso@gmail.com")
                    .password(encoder.encode("123"))
                    .authorities(new HashSet<>())
                    .enabled(true)
                    .build();


            Authority userRole = authorityService.findByRole(Authority.Roles.USER).orElseThrow();
            Authority adminRole = authorityService.findByRole(Authority.Roles.ADMIN).orElseThrow();

            admin.addRole(userRole);
            admin.addRole(adminRole);

            userRepository.save(admin);

        };
    }

}
