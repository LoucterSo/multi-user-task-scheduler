package io.github.LoucterSo.task_tracker_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.hamcrest.core.IsNot;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final User user = new User();
    private final Authority authority = new Authority();
    private final TaskDto taskDto = new TaskDto(1L, "title1", "desc1", false, null);

    @BeforeEach
    void setUp() {
        authority.setRole(Authority.Roles.USER);
        setUpSecurityContext();

        jdbcTemplate.execute("truncate table users, roles, tasks restart identity cascade");
        jdbcTemplate.execute(
                "insert into users (user_id, first_name, last_name, email, password, enabled) "
                        + "values (1, 'first', 'last', 'test@gmail.com', 'test pass', true)");
        jdbcTemplate.execute(
                "insert into tasks (title, description, user_id, done, completion_time) "
                        + "values ('title1', 'desc1', 1, false, null), ('title2', 'desc2', 1, true, CURRENT_TIMESTAMP)");
    }

    @Test
    void shouldGetCurrentUserTasks() throws Exception {
        // given
        var requestBuilder =
                get("/tasks")
                        .with(
                                SecurityMockMvcRequestPostProcessors.securityContext(
                                        SecurityContextHolder.getContext()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[*].id", containsInRelativeOrder(1, 2)),
                        jsonPath("$[*].title", containsInRelativeOrder("title1", "title2")),
                        jsonPath("$[*].description", containsInRelativeOrder("desc1", "desc2")),
                        jsonPath("$[*].done", containsInRelativeOrder(false, true)),
                        jsonPath("$[*].completion_time", containsInRelativeOrder(IsNull.nullValue(), IsNull.notNullValue()))
                );
    }

    @Test
    void shouldCreateNewTask() throws Exception {
        // given
        var requestBuilder =
                MockMvcRequestBuilders.post("/tasks")
                        .with(
                                SecurityMockMvcRequestPostProcessors.securityContext(
                                        SecurityContextHolder.getContext()))
                        .content(mapper.writeValueAsString(taskDto))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isCreated(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.id").value(3),
                        jsonPath("$.title").value("title1"),
                        jsonPath("$.description").value("desc1"),
                        jsonPath("$.done").value(false),
                        jsonPath("$.completion_time").value(IsNull.nullValue())
                );

        Integer actual =
                jdbcTemplate.queryForObject(
                        "select count(*) from tasks where task_id = 3", Integer.class);
        Assertions.assertEquals(1, actual);
    }

    @Test
    void shouldUpdateTask() throws Exception {
        // given
        TaskDto toUpdate = new TaskDto(1L, "newTitle", "newDesc", true, Timestamp.valueOf(LocalDateTime.now()));

        var requestBuilder =
                put("/tasks/{id}", 1L)
                        .with(
                                SecurityMockMvcRequestPostProcessors.securityContext(
                                        SecurityContextHolder.getContext()))
                        .content(mapper.writeValueAsString(toUpdate))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value("newTitle"),
                        jsonPath("$.description").value("newDesc"),
                        jsonPath("$.done").value(true),
                        jsonPath("$.completion_time").value(IsNull.notNullValue())
                );
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldDeleteTask() throws Exception {
        // given
        var requestBuilder =
                delete("/tasks/{id}", 2)
                        .with(
                                SecurityMockMvcRequestPostProcessors.securityContext(
                                        SecurityContextHolder.getContext()));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        // then
        Integer actual =
                jdbcTemplate.queryForObject(
                        "select count(*) from tasks where task_id = 2", Integer.class);
        Assertions.assertEquals(0, actual);
    }

    private void setUpSecurityContext() {
        user.setId(1L);
        user.setEmail("test@gmail.com");
        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, null, Set.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
