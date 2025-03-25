package io.github.LoucterSo.task_tracker_backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.api.controller.auth.AuthRestController;
import io.github.LoucterSo.task_tracker_backend.config.SecurityConfig;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthServiceImpl;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "TestPassword1.";
    private static final String TEST_FIRST_NAME = "First Name";
    private static final String TEST_LAST_NAME = "Last Name";
    private static final String ACCESS_TOKEN = "accessToken";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserServiceImpl userService;

    private final AuthResponseForm response = new AuthResponseForm(ACCESS_TOKEN);

    @Test
    void signup_shouldResponseWithAccessTokenAndOkStatusCode() throws Exception {
        // given
        SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);
        var requestBuilder =
                post("/auth/signup")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        when(authService.register(any(SignupForm.class), any(HttpServletResponse.class)))
                .thenReturn(response);
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.access_token").value(response.accessToken()))
                .andExpect(status().isCreated());

        verify(authService).register(any(SignupForm.class), any(HttpServletResponse.class));
    }

    @Test
    void login_shouldResponseWithAccessTokenAndOkStatusCode() throws Exception {
        // given
        LoginForm loginReq = new LoginForm(TEST_EMAIL, TEST_PASSWORD);
        var requestBuilder =
                post("/auth/login")
                        .content(mapper.writeValueAsString(loginReq))
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        when(authService.login(any(LoginForm.class), any(HttpServletResponse.class)))
                .thenReturn(response);
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.access_token").value(response.accessToken()))
                .andExpect(status().isOk());

        verify(authService).login(any(LoginForm.class), any(HttpServletResponse.class));
    }


    @Test
    void logout_shouldReturnOkStatusCode() throws Exception {
        // given
        var requestBuilder =
                post("/auth/logout");

        // when
        doNothing().when(authService)
                .logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(status().isOk());

        verify(authService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void refreshToken_shouldResponseWithAccessTokenAndOkStatusCode() throws Exception {
        // given
        var requestBuilder =
                post("/auth/refresh-token");

        // when
        when(authService.refreshToken(any(HttpServletRequest.class)))
                .thenReturn(response);
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.access_token").value(response.accessToken()))
                .andExpect(status().isOk());

        verify(authService).refreshToken(any(HttpServletRequest.class));
    }
}


