package io.github.LoucterSo.task_tracker_backend.user;

import io.github.LoucterSo.task_tracker_backend.entity.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.User;
import io.github.LoucterSo.task_tracker_backend.repository.AuthorityRepository;
import io.github.LoucterSo.task_tracker_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:13-alpine://backend",
        "spring.datasource.username=postgres",
        "spring.datasource.password=123"
})
public class UserRepositoryTCLiveTest {

    @Container
//    @ServiceConnection убирает нужду в конфигурации, но у меня почему то нет доступа к этой аннотации
    private PostgreSQLContainer<?> postgresdb = new PostgreSQLContainer<>()
            .withDatabaseName("backend")
            .withUsername("postgres")
            .withPassword("123");

//    @DynamicPropertySource
//    static void register(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> postgresdb.getJdbcUrl());
//        registry.add("spring.datasource.username", () -> postgresdb.getUsername());
//        registry.add("spring.datasource.password", () -> postgresdb.getPassword());
//    }
//
//    @BeforeEach
//    void setUp() {
//        postgresdb.start();
//    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void containerShouldBeCreatedAndRunning() {
        assertThat(postgresdb.isCreated()).isTrue();
        assertThat(postgresdb.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        for (Authority.Roles role : Authority.Roles.values()) {
            authorityRepository.findByRole(role).orElseGet(() -> {
                Authority authority = new Authority(role);
                authorityRepository.save(authority);
                return authority;
            });
        }
    }

    @Test
    void testSimpleSaveAndFind() {
        Authority authority = authorityRepository.findByRole(Authority.Roles.USER).orElseThrow();

        User newUser = userRepository.save(User.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .email("vlad_gorelkin@inbox.ru")
                .password("123124")
                .enabled(true)
                .authorities(Set.of(authority))
                .build());

        User foundUser = userRepository.findById(newUser.getId()).orElseThrow();
        assertThat(newUser).isEqualTo(foundUser);

        List<Authority> foundAuthorities = authorityRepository.findAll();
        assertThat(foundAuthorities.size()).isEqualTo(2);
        assertThat(foundAuthorities).contains(authority);

        userRepository.deleteAll();

        assertThat(userRepository.findById(newUser.getId()).isEmpty()).isTrue();
        assertThat(userRepository.findAll().size()).isEqualTo(0);

        assertThat(authorityRepository.findAll().size()).isEqualTo(2);
        assertThat(authorityRepository.findAll()).contains(authority);
    }

//    @AfterAll
//    @BeforeEach
//    void shuDown() {
//        postgresdb.start();
//    }
}
