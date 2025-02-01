package io.github.LoucterSo.task_tracker_backend;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.repository.AuthorityRepository;
import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class TaskTrackerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder encoder) {

        return runner -> {
            userRepository.deleteAll();

            User admin = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("loucterso@gmail.com")
                    .password(encoder.encode("123"))
                    .authorities(new HashSet<>())
                    .enabled(true)
                    .build();

            Authority adminAuthority = new Authority();
            adminAuthority.setRole(Authority.Roles.ADMIN);
            admin.addRole(adminAuthority);

            Authority userAuthority = new Authority();
            adminAuthority.setRole(Authority.Roles.USER);
            admin.addRole(userAuthority);

            userRepository.save(admin);

        };
    }

}
