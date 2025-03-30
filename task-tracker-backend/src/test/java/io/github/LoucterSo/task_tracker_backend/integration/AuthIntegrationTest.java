package io.github.LoucterSo.task_tracker_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Authority authority = new Authority();
    private final SignupForm req = new SignupForm("FirstName", "LastName", "test@gmail.com", "testPass");

    @BeforeEach
    void setUp() {
        authority.setRole(Authority.Roles.USER);

        jdbcTemplate.execute("truncate table users, roles, tasks restart identity cascade");

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(new CompletableFuture<>());
    }

    @Test
    void shouldSignupUser() throws Exception {
        // given
        var requestBuilder =
                post("/auth/signup")
                        .with(user("username").authorities(authority))
                        .content(mapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(status().isCreated());
    }

    @Test
    void givenDuplicateUser_whenRegisterUser_thenReturnConflict() throws Exception {
        // given
        jdbcTemplate.execute(
                "insert into users (first_name, last_name, email, password, enabled) "
                        + "values ('first', 'last', 'test@gmail.com', 'test pass', true)");

        var requestBuilder =
                post("/auth/signup")
                        .with(user("username").authorities(authority))
                        .content(mapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(status().isConflict());
    }
}
