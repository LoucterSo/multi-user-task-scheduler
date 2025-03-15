package io.github.LoucterSo.task_tracker_backend.user;

import io.github.LoucterSo.task_tracker_backend.api.rest_controller.UserRestController;
import io.github.LoucterSo.task_tracker_backend.entity.user.Authority;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.user.UserResponseForm;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @MockitoBean
    private UserService userService;

    //JwtService не создается в контексте
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRestController userRestController;

    @Test
    void contextLoads() {
        assertThat(userRestController).isNotNull();
    }

    @Test
    void getCurrentUserData_shouldResponseWithCurrentUserDataAndOkStatusCode() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("email@someemail.com");

        SecurityContext securityContext = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(Authority.Roles.USER)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserResponseForm expected = new UserResponseForm(mockUser.getId(), mockUser.getEmail());
        when(userService.createUserResponseForm(mockUser)).thenReturn(expected);

        this.mockMvc.perform(get("/users/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()));

    }

    @Test
    void getAllUserData_shouldResponseWithAllUserDataAndOkStatusCode() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("email@someemail.com");

        SecurityContext securityContext = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Set.of(new Authority(1, Authority.Roles.USER), new Authority(2, Authority.Roles.ADMIN)));
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp time = Timestamp.from(zonedDateTime.toInstant());

        List<UserResponseForm> expectedUserData = Stream.of(
                        new User(1L, "", "", "", "email1@someemail.com", true, null, null, time, time),
                        new User(2L, "", "", "", "email2@someemail.com", true, null, null, time, time))
                .map(u -> new UserResponseForm(u.getId(), u.getEmail()))
                .toList();
        when(userService.getAllUsers()).thenReturn(expectedUserData);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("email1@someemail.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("email2@someemail.com"));

    }
}
