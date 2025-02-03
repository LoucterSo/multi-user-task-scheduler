package io.github.LoucterSo.task_tracker_backend.config;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import io.github.LoucterSo.task_tracker_backend.service.authority.AuthorityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class Initialization {

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

            if (!userRepository.existsByEmail("loucterso@gmail.com")) {
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
            }

        };
    }

}
