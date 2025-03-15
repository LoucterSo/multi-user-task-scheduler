package io.github.LoucterSo.task_tracker_backend.config;

import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.repository.user.AuthorityRepository;
import io.github.LoucterSo.task_tracker_backend.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class Initialization {

    @Bean
    public CommandLineRunner commandLineRunner(
            UserRepository userRepository,
            AuthorityRepository authRepository,
            PasswordEncoder encoder
    ) {

        return runner -> {

//            for (Authority.Roles role : Authority.Roles.values()) {
//                authorityService.findByRole(role).orElseGet(() -> {
//                    Authority authority = new Authority(role);
//                    authorityService.save(authority);
//                    return authority;
//                });
//            }

            if (!userRepository.existsByEmail("loucterso@gmail.com")) {

                User admin = User.builder()
                        .firstName("admin")
                        .lastName("admin")
                        .email("loucterso@gmail.com")
                        .password(encoder.encode("123"))
                        .enabled(true)
                        .build();
                Authority userRole = Authority.builder().role(Authority.Roles.USER).user(admin).build();
                Authority adminRole = Authority.builder().role(Authority.Roles.ADMIN).user(admin).build();
                admin.setAuthorities(Set.of(userRole, adminRole));

                userRepository.save(admin);
                authRepository.save(userRole);
                authRepository.save(adminRole);
            }

        };
    }

}
