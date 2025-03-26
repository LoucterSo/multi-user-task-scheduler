package io.github.LoucterSo.task_tracker_backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.api.controller.user.UserRestController;
import io.github.LoucterSo.task_tracker_backend.config.SecurityConfig;
import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithTasksDto;
import io.github.LoucterSo.task_tracker_backend.form.user.UserWithoutTasks;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserServiceImpl;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRST_NAME = "First Name";
    private static final String TEST_LAST_NAME = "Last Name";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserServiceImpl userService;

    private final User mockCurrentUser = User.builder()
            .id(TEST_ID)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .enabled(true)
            .build();

    private final Set<Task> tasks = Set.of(
            Task.builder()
                    .id(1L)
                    .title("Title1")
                    .description("Desc1")
                    .isDone(false)
                    .completionTime(null).build(),
            Task.builder()
                    .id(2L)
                    .title("Title2")
                    .description("Desc2")
                    .isDone(false)
                    .completionTime(null).build()
    );

    private final List<UserWithTasksDto> allUsers = List.of(
            UserWithTasksDto.fromEntity(User.builder()
                    .id(TEST_ID + 1)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .email(TEST_EMAIL)
                    .tasks(tasks)
                    .enabled(true)
                    .build()),
            UserWithTasksDto.fromEntity(User.builder()
                    .id(TEST_ID + 2)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .email(TEST_EMAIL)
                    .enabled(true)
                    .build())
    );

    @Nested
    class GetCurrentUserData {
        @Test
        void getCurrentUserData_shouldResponseWithCurrentUserDataAndOkStatusCode() throws Exception {
            // given
            setUpSecurityContext(new Authority(Authority.Roles.USER));
            User mockUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var requestBuilder =
                    get("/users/current")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));

            // when
            try (MockedStatic<UserWithoutTasks> mockedStatic = Mockito.mockStatic(UserWithoutTasks.class)) {
                mockedStatic.when(() -> UserWithoutTasks.fromEntity(mockUser))
                        .thenReturn(new UserWithoutTasks(
                                mockUser.getId(),
                                mockUser.getEmail(),
                                mockUser.getFirstName(),
                                mockUser.getLastName()));
                mockMvc.perform(requestBuilder)
                        // then
                        .andExpectAll(
                                content().contentType(MediaType.APPLICATION_JSON),
                                status().isOk(),
                                jsonPath("$.length()").value(4),
                                jsonPath("$.id").value(TEST_ID),
                                jsonPath("$.email").value(TEST_EMAIL),
                                jsonPath("$.first_name").value(TEST_FIRST_NAME),
                                jsonPath("$.last_name").value(TEST_LAST_NAME));

                mockedStatic.verify(() -> UserWithoutTasks.fromEntity(mockUser));
            }
        }
    }

    @Nested
    class GetAllUsersWithTasks {
        @Test
        void getAllUsersWithTasks_shouldResponseWithAllUsersWithTasksAndOkStatusCode() throws Exception {
            // given
            setUpSecurityContext(new Authority(Authority.Roles.ADMIN));
            var requestBuilder =
                    get("/users")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));

            // when
            when(userService.getAllUsersWithTasks())
                    .thenReturn(allUsers);
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            content().contentType(MediaType.APPLICATION_JSON),
                            status().isOk(),
                            jsonPath("$.length()").value(2),
                            jsonPath("$[0].id").value(TEST_ID + 1),
                            jsonPath("$[0].email").value(TEST_EMAIL),
                            jsonPath("$[0].first_name").value(TEST_FIRST_NAME),
                            jsonPath("$[0].last_name").value(TEST_LAST_NAME),
                            jsonPath("$[0].tasks[*]", hasSize(2)),
                            jsonPath("$[0].tasks[*].title", containsInAnyOrder("Title1", "Title2")),
                            jsonPath("$[0].tasks[*].description", containsInAnyOrder("Desc1", "Desc2")),
                            jsonPath("$[0].tasks[*].done", everyItem(is(false))),
                            jsonPath("$[0].tasks[*].completion_time",  everyItem(is(IsNull.nullValue()))),
                            jsonPath("$[1].id").value(TEST_ID + 2),
                            jsonPath("$[1].email").value(TEST_EMAIL),
                            jsonPath("$[1].first_name").value(TEST_FIRST_NAME),
                            jsonPath("$[1].last_name").value(TEST_LAST_NAME),
                            jsonPath("$[1].tasks", empty())
                    );

            verify(userService).getAllUsersWithTasks();
        }

        @Test
        void getAllUsersWithTasks_shouldResponseWithErrorResponseAndForbiddenStatusCode() throws Exception {
            // given
            setUpSecurityContext(new Authority(Authority.Roles.USER));
            var requestBuilder =
                    get("/users")
                            .with(
                                    SecurityMockMvcRequestPostProcessors
                                            .securityContext(SecurityContextHolder.getContext()));

            // when
            mockMvc.perform(requestBuilder)
                    // then
                    .andExpectAll(
                            content().contentType(MediaType.APPLICATION_JSON),
                            status().isForbidden(),
                            jsonPath("$.length()").value(2),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timeStamp").exists()
                    );

            verifyNoMoreInteractions(userService);
        }

    }

    private void setUpSecurityContext(Authority authority) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockCurrentUser, null, Set.of(authority));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
