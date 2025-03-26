package io.github.LoucterSo.task_tracker_backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.api.controller.auth.AuthRestController;
import io.github.LoucterSo.task_tracker_backend.config.SecurityConfig;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private static final String TEST_FIRST_NAME = "FirstName";
    private static final String TEST_LAST_NAME = "LastName";
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

    @Nested
    class SignupTests {

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
                    .andExpectAll(
                            jsonPath("$.length()").value(1),
                            jsonPath("$.access_token").value(response.accessToken()),
                            status().isCreated());

            verify(authService).register(any(SignupForm.class), any(HttpServletResponse.class));
        }
        
        @Nested
        class ValidationTests {
            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenFirstNameIsEmpty() throws Exception {
                SignupForm request = new SignupForm("", TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.firstName").value("First name cannot be empty"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenFirstNameIsTooShort() throws Exception {
                SignupForm request = new SignupForm("Jo", TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.firstName").value("First name must be between 3 and 64 characters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenFirstNameIsTooLong() throws Exception {
                String longName = "A".repeat(65);
                SignupForm request = new SignupForm(longName, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.firstName").value("First name must be between 3 and 64 characters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenFirstNameContainsNumbers() throws Exception {
                SignupForm request = new SignupForm("John1", TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.firstName").value("First name must include only letters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenLastNameIsEmpty() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, "", TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.lastName").value("Last name cannot be empty"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenLastNameIsTooShort() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, "Do", TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.lastName").value("Last name must be between 3 and 64 characters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenLastNameIsTooLong() throws Exception {
                String longName = "A".repeat(65);
                SignupForm request = new SignupForm(TEST_FIRST_NAME, longName, TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.lastName").value("Last name must be between 3 and 64 characters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenLastNameContainsNumbers() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, "Doe2", TEST_EMAIL, TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.lastName").value("Last name must include only letters"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenEmailIsEmpty() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, "", TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.email").value("Email cannot be empty"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenEmailIsInvalid() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, "invalid-email", TEST_PASSWORD);

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.email").value("Invalid email"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenPasswordIsEmpty() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, "");

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.password").value("Password cannot be empty"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenPasswordIsTooShort() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, "short");

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.password").value("Password must be 8 characters or more"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenPasswordHasNoUppercase() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, "lowercase1");

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.password").value("Password must contain at least one uppercase and one lowercase letter"));
            }

            @Test
            void signup_shouldReturnBadRequestStatusCodeWhenPasswordHasNoLowercase() throws Exception {
                SignupForm request = new SignupForm(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, "UPPERCASE1");

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.password").value("Password must contain at least one uppercase and one lowercase letter"));
            }
        }
    }


    @Nested
    class LoginTests {
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
                    .andExpectAll(
                            jsonPath("$.length()").value(1),
                            jsonPath("$.access_token").value(response.accessToken()),
                            status().isOk());

            verify(authService).login(any(LoginForm.class), any(HttpServletResponse.class));
        }

        @Nested
        class ValidationTests {
            @Test
            void login_shouldReturnBadRequestStatusCodeWhenEmailIsEmpty() throws Exception {
                LoginForm request = new LoginForm("", TEST_PASSWORD);

                mockMvc.perform(post("/auth/login")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.email").value("Email cannot be empty"));
            }

            @Test
            void login_shouldReturnBadRequestStatusCodeWhenPasswordIsEmpty() throws Exception {
                LoginForm request = new LoginForm(TEST_EMAIL, "");

                mockMvc.perform(post("/auth/signup")
                                .content(mapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorFields.password").value("Password cannot be empty"));
            }
        }
    }


    @Nested
    class logoutTests {
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
    }

    @Nested
    class RefreshTokenTests {
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
                    .andExpectAll(
                            jsonPath("$.length()").value(1),
                            jsonPath("$.access_token").value(response.accessToken()),
                            status().isOk());

            verify(authService).refreshToken(any(HttpServletRequest.class));
        }
    }
}


