package io.github.LoucterSo.task_tracker_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.api.controller.task.TaskRestController;
import io.github.LoucterSo.task_tracker_backend.config.SecurityConfig;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserServiceImpl;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(TaskRestController.class)
class TaskRestControllerTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "TestPassword1.";
    private static final String TEST_FIRST_NAME = "First Name";
    private static final String TEST_LAST_NAME = "Last Name";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private UserServiceImpl userService;

    private final User mockUser = User.builder()
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .password(TEST_PASSWORD)
            .authorities(Set.of(new Authority(Authority.Roles.USER)))
            .enabled(true).build();

    private final ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
    private final Timestamp time = Timestamp.from(zonedDateTime.toInstant());
    private final List<TaskDto> tasks = new ArrayList<>(List.of(
            new TaskDto(1L, "Title1", "Desc1", false, null),
            new TaskDto(2L, "Title2", "Desc2", true, time),
            new TaskDto(3L, "Title3", "Desc3", false, null)
    ));

    private final TaskDto invalidTask = new TaskDto(4L, "", "Desc4", false, null);

    @BeforeEach
    void setUp() {
        setUpSecurityContext();
    }

    @Nested
    class GetUserTasksTests {

        @Test
        void getUserTasks_shouldGetCurrentUserTasks() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    get("/tasks")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));
            String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

            // when
            when(userService.findById(mockUser.getId()))
                    .thenReturn(Optional.of(mockUser));
            when(taskService.getUserTasks(any(User.class)))
                    .thenReturn(tasks);
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.length()").value(3),
                            jsonPath("$[0].id").value(1L),
                            jsonPath("$[0].title").value("Title1"),
                            jsonPath("$[0].description").value("Desc1"),
                            jsonPath("$[0].done").value(false),
                            jsonPath("$[0].completion_time").value(IsNull.nullValue()),
                            jsonPath("$[1].id").value(2L),
                            jsonPath("$[1].title").value("Title2"),
                            jsonPath("$[1].description").value("Desc2"),
                            jsonPath("$[1].done").value(true),
                            jsonPath("$[1].completion_time").value(expectedTime),
                            jsonPath("$[2].id").value(3L),
                            jsonPath("$[2].title").value("Title3"),
                            jsonPath("$[2].description").value("Desc3"),
                            jsonPath("$[2].done").value(false),
                            jsonPath("$[2].completion_time").value(IsNull.nullValue()));

            verify(userService).findById(mockUser.getId());
            verify(taskService).getUserTasks(any(User.class));
        }

        @Test
        void getUserTasks_shouldResponseWithErrorResponseAndUnauthorizedStatusCode() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    get("/tasks")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));

            // when
            when(userService.findById(mockUser.getId()))
                    .thenReturn(Optional.empty());
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(2),
                            jsonPath("$.message").value("Current user not found"),
                            status().isUnauthorized());

            verify(userService).findById(mockUser.getId());
            verifyNoMoreInteractions(taskService);
        }
    }

    @Nested
    class GetUserTaskTests {
        @Test
        void getUserTask_shouldGetUserTaskByTaskId() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    get("/tasks/2")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));
            String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

            // when
            when(taskService.userHasTask(mockUser, 2L))
                    .thenReturn(true);
            when(taskService.getUserTaskById(mockUser, 2L))
                    .thenReturn(tasks.get(1));
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(5),
                            jsonPath("$.id").value(2L),
                            jsonPath("$.title").value("Title2"),
                            jsonPath("$.description").value("Desc2"),
                            jsonPath("$.done").value(true),
                            jsonPath("$.completion_time").value(expectedTime),
                            status().isOk());

            verify(taskService).getUserTaskById(mockUser, 2L);
        }

        @Test
        void getUserTask_shouldResponseWithErrorResponseAndForbiddenStatusCode() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    get("/tasks/2")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));

            // when
            when(taskService.userHasTask(mockUser, 2L))
                    .thenReturn(false);
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.length()").value(2),
                            jsonPath("$.message").value("User doesn't have rights to get this task"),
                            status().isForbidden());

            verify(taskService).userHasTask(mockUser, 2L);
            verifyNoMoreInteractions(taskService);
        }
    }

    @Nested
    class CreateTasksTests {

        @Test
        void createTask_shouldCreateNewTask() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    post("/tasks")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(tasks.get(0)))
                            .contentType(MediaType.APPLICATION_JSON);

            // when
            when(taskService.saveTask(mockUser, tasks.get(0)))
                    .thenReturn(tasks.get(0));
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(5),
                            jsonPath("$.id").value(1),
                            jsonPath("$.title").value("Title1"),
                            jsonPath("$.description").value("Desc1"),
                            jsonPath("$.done").value(false),
                            jsonPath("$.completion_time").value(IsNull.nullValue()),
                            status().isCreated());

            verify(taskService).saveTask(mockUser, tasks.get(0));
        }

        @Test
        void createTask_shouldResponseWithErrorResponseAndBadRequestStatusCode() throws Exception {
            // given
            var requestBuilder =
                    post("/tasks")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(invalidTask))
                            .contentType(MediaType.APPLICATION_JSON);
            ;

            // when
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(2),
                            jsonPath("$.errorFields.title").value("Title cannot be empty"),
                            status().isBadRequest());

            verifyNoMoreInteractions(taskService);
        }
    }

    @Nested
    class UpdateTasksTests {

        @Test
        void updateTask_shouldUpdateTaskById() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    put("/tasks/1")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(tasks.get(0)))
                            .contentType(MediaType.APPLICATION_JSON);

            // when
            when(taskService.userHasTask(mockUser, 1L))
                    .thenReturn(true);
            when(taskService.updateTask(tasks.get(0), 1L))
                    .thenReturn(tasks.get(0));
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(5),
                            jsonPath("$.id").value(1),
                            jsonPath("$.title").value("Title1"),
                            jsonPath("$.description").value("Desc1"),
                            jsonPath("$.done").value(false),
                            jsonPath("$.completion_time").value(IsNull.nullValue()),
                            status().isOk());

            verify(taskService).userHasTask(mockUser, 1L);
            verify(taskService).updateTask(tasks.get(0), 1L);
        }

        @Test
        void updateTask_shouldResponseWithErrorResponseAndForbiddenStatusCode() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    put("/tasks/2")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(tasks.get(1)))
                            .contentType(MediaType.APPLICATION_JSON);

            // when
            when(taskService.userHasTask(mockUser, 2L))
                    .thenReturn(false);
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(2),
                            jsonPath("$.message").value("User doesn't have rights to change this task"),
                            status().isForbidden());

            verify(taskService).userHasTask(mockUser, 2L);
            verifyNoMoreInteractions(taskService);
        }

        @Test
        void updateTask_shouldResponseWithErrorResponseAndBadRequestStatusCode() throws Exception {
            // given
            var requestBuilder =
                    put("/tasks/4")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(invalidTask))
                            .contentType(MediaType.APPLICATION_JSON);
            ;

            // when
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(2),
                            jsonPath("$.errorFields.title").value("Title cannot be empty"),
                            status().isBadRequest());

            verifyNoMoreInteractions(taskService);
        }
    }

    @Nested
    class DeleteTasksTests {
        @Test
        void deleteTask_shouldDeleteTaskById() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    delete("/tasks/2")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));
            String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

            // when
            when(taskService.userHasTask(mockUser, 2L))
                    .thenReturn(true);
            when(taskService.deleteTaskById(2L))
                    .thenReturn(tasks.get(1));
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(5),
                            jsonPath("$.id").value(2),
                            jsonPath("$.title").value("Title2"),
                            jsonPath("$.description").value("Desc2"),
                            jsonPath("$.done").value(true),
                            jsonPath("$.completion_time").value(expectedTime),
                            status().isOk());

            verify(taskService).userHasTask(mockUser, 2L);
            verify(taskService).deleteTaskById(2L);
        }

        @Test
        void deleteTask_shouldResponseWithErrorResponseAndForbiddenStatusCode() throws Exception {
            // given
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    delete("/tasks/2")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()))
                            .content(mapper.writeValueAsString(tasks.get(1)))
                            .contentType(MediaType.APPLICATION_JSON);

            // when
            when(taskService.userHasTask(mockUser, 2L))
                    .thenReturn(false);
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            jsonPath("$.length()").value(2),
                            jsonPath("$.message").value("User doesn't have rights to delete this task"),
                            status().isForbidden());

            verify(taskService).userHasTask(mockUser, 2L);
            verifyNoMoreInteractions(taskService);
        }
    }

    private void setUpSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
