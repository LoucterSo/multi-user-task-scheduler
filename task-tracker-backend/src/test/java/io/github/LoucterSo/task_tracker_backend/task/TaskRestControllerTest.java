package io.github.LoucterSo.task_tracker_backend.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.LoucterSo.task_tracker_backend.api.rest_controller.TaskRestController;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskForm;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.task.TaskService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;
// Работает, но только с .with(SecurityMockMvcRequestPostProcessors.csrf())). У меня отключена эта защита, почему так?

@WebMvcTest(TaskRestController.class)
public class TaskRestControllerTest {

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskRestController taskRestController;

    //JwtService не создается в контексте
    @MockitoBean
    private JwtService jwtService;

    @Test
    void contextLoads() {
        assertThat(taskRestController).isNotNull();
    }

    @Test
    //@WithMockUser(username = "user", roles = "USER") не работает!!!
    void getUserTasks_shouldResponseWithListOfUserTasksAndOkStatusCode() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        List<TaskResponseForm> tasks = Stream.of(
                new Task(1L, "Task1", "Description1", mockUser, false, time, time, null),
                new Task(2L, "Task2", "Done", mockUser, true, time, time, time),
                new Task(3L, "Task3", "", mockUser, true, time, time, time)
        )
                .map(TaskResponseForm::new)
                .toList();

        when(userService.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(taskService.getUserTasks(any(User.class))).thenReturn(tasks);

        String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

        this.mockMvc
                .perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Task1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].done").value(false))
                .andExpect(jsonPath("$[0].created").value(expectedTime))
                .andExpect(jsonPath("$[0].updated").value(expectedTime))
                .andExpect(jsonPath("$[0].completionTime").isEmpty())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].userId").value(1L))
                .andExpect(jsonPath("$[1].title").value("Task2"))
                .andExpect(jsonPath("$[1].description").value("Done"))
                .andExpect(jsonPath("$[1].done").value(true))
                .andExpect(jsonPath("$[1].created").value(expectedTime))
                .andExpect(jsonPath("$[1].updated").value(expectedTime))
                .andExpect(jsonPath("$[1].completionTime").value(expectedTime))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].userId").value(1L))
                .andExpect(jsonPath("$[2].title").value("Task3"))
                .andExpect(jsonPath("$[2].description").value(""))
                .andExpect(jsonPath("$[2].done").value(true))
                .andExpect(jsonPath("$[2].created").value(expectedTime))
                .andExpect(jsonPath("$[2].updated").value(expectedTime))
                .andExpect(jsonPath("$[2].completionTime").value(expectedTime));
    }

    @Test
    void getUserTask_shouldResponseWithUserTaskAndOkStatusCode() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        TaskResponseForm expected = new TaskResponseForm(new Task(1L, "Task1", "Description1", mockUser, false, time, time, null));

        when(taskService.getUserTaskById(any(User.class), anyLong())).thenReturn(expected);

        String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

        this.mockMvc
                .perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("Task1"))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.created").value(expectedTime))
                .andExpect(jsonPath("$.updated").value(expectedTime))
                .andExpect(jsonPath("$.completionTime").isEmpty());
    }

    @Test
    void createTask_shouldResponseWithCreatedTaskAndCreatedStatusCode() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        TaskForm taskForm = new TaskForm("NewTask", "NewDes", false);
        TaskResponseForm expected = new TaskResponseForm(new Task(1L, taskForm.getTitle(), taskForm.getDescription(),
                mockUser, taskForm.isDone(), time, time, null));

        when(taskService.saveTask(any(User.class), any(TaskForm.class))).thenReturn(expected);

        String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonTask = ow.writeValueAsString(taskForm);

        this.mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonTask)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.title").value("NewTask"))
                .andExpect(jsonPath("$.description").value("NewDes"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.created").value(expectedTime))
                .andExpect(jsonPath("$.updated").value(expectedTime))
                .andExpect(jsonPath("$.completionTime").isEmpty());
    }

    @Test
    void updateTask_shouldResponseWithCreatedTaskAndOkStatusCode() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        TaskForm taskForm = new TaskForm("ChangedTask", "ChangedDes", true);
        TaskResponseForm expected = new TaskResponseForm(new Task(1L, taskForm.getTitle(), taskForm.getDescription(),
                mockUser, taskForm.isDone(), time, time, time));

        when(taskService.userHasTask(any(User.class), anyLong())).thenReturn(true);
        when(taskService.updateTask(any(TaskForm.class), anyLong())).thenReturn(expected);

        String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonTask = ow.writeValueAsString(taskForm);

        this.mockMvc
                .perform(
                        put("/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonTask)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.description").value(expected.getDescription()))
                .andExpect(jsonPath("$.done").value(expected.isDone()))
                .andExpect(jsonPath("$.created").value(expectedTime))
                .andExpect(jsonPath("$.updated").value(expectedTime))
                .andExpect(jsonPath("$.completionTime").value(expectedTime));
    }

    @Test
    void deleteTask_shouldResponseWithCreatedTaskAndOkStatusCode() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        TaskResponseForm expected = new TaskResponseForm(new Task(1L, "DeletedTask", "DeletedDes",
                mockUser, true, time, time, time));

        when(taskService.userHasTask(any(User.class), anyLong())).thenReturn(true);
        when(taskService.deleteTaskById(anyLong())).thenReturn(expected);

        String expectedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx").format(zonedDateTime);

        this.mockMvc
                .perform(
                        delete("/tasks/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.description").value(expected.getDescription()))
                .andExpect(jsonPath("$.done").value(expected.isDone()))
                .andExpect(jsonPath("$.created").value(expectedTime))
                .andExpect(jsonPath("$.updated").value(expectedTime))
                .andExpect(jsonPath("$.completionTime").value(expectedTime));
    }
}
