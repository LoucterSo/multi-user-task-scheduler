package io.github.LoucterSo.task_tracker_backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.LoucterSo.task_tracker_backend.api.rest_controller.AuthRestController;
import io.github.LoucterSo.task_tracker_backend.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_backend.form.auth.SignupForm;
import io.github.LoucterSo.task_tracker_backend.service.auth.AuthService;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtService;
import io.github.LoucterSo.task_tracker_backend.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthRestController.class)
public class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRestController authRestController;

    //JwtService не создается в контексте
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @Test
    void contextLoads() {
        assertThat(authRestController).isNotNull();
    }

    @Test
    void register_shouldResponseWithAccessTokenCreatedStatusCodeAndRefreshTokenPutInCookie() throws Exception {

        SignupForm signupForm = SignupForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .password("123")
                .email("email@someemail.com")
                .build();
        AuthResponseForm authResponseForm = new AuthResponseForm("accessToken");
        String refreshToken = "refreshToken";

        when(authService.register(any(SignupForm.class), any(HttpServletResponse.class))).thenReturn(authResponseForm);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setMaxAge(10000);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);

        MockHttpServletResponse response = this.mockMvc.perform(post("/auth/signup")
                        .cookie(refreshTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupForm))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token")
                        .value(authResponseForm.getAccessToken()))
                .andReturn().getResponse();

        Cookie cookieValue = response.getCookie("refreshToken");
        assertThat(cookieValue).isNotNull();
        assertThat(cookieValue.getValue()).isEqualTo(refreshToken);
    }

//    @Test
//    void login_shouldResponseWithUserTaskAndOkStatusCode() throws Exception {
//
//        SignupForm signupForm = SignupForm.builder()
//                .firstName("First Name")
//                .lastName("Last Name")
//                .password("123")
//                .email("email@someemail.com")
//                .build();
//        AuthResponseForm authResponseForm = new AuthResponseForm("accessToken");
//        String refreshToken = "refreshToken";
//
//        when(authService.register(any(SignupForm.class), any(HttpServletResponse.class))).thenReturn(authResponseForm);
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setMaxAge(10000);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setSecure(true);
//
//        MockHttpServletResponse response = this.mockMvc.perform(post("/auth/signup")
//                        .cookie(refreshTokenCookie)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(signupForm))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.access_token")
//                        .value(authResponseForm.getAccessToken()))
//                .andReturn().getResponse();
//
//        Cookie cookieValue = response.getCookie("refreshToken");
//        assertThat(cookieValue).isNotNull();
//        assertThat(cookieValue.getValue()).isEqualTo(refreshToken);
//    }
//
//    @Test
//    void logout_shouldResponseWithUserTaskAndOkStatusCode() throws Exception {
//
//        SignupForm signupForm = SignupForm.builder()
//                .firstName("First Name")
//                .lastName("Last Name")
//                .password("123")
//                .email("email@someemail.com")
//                .build();
//        AuthResponseForm authResponseForm = new AuthResponseForm("accessToken");
//        String refreshToken = "refreshToken";
//
//        when(authService.register(any(SignupForm.class), any(HttpServletResponse.class))).thenReturn(authResponseForm);
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setMaxAge(10000);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setSecure(true);
//
//        MockHttpServletResponse response = this.mockMvc.perform(post("/auth/signup")
//                        .cookie(refreshTokenCookie)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(signupForm))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.access_token")
//                        .value(authResponseForm.getAccessToken()))
//                .andReturn().getResponse();
//
//        Cookie cookieValue = response.getCookie("refreshToken");
//        assertThat(cookieValue).isNotNull();
//        assertThat(cookieValue.getValue()).isEqualTo(refreshToken);
//    }
//
//    @Test
//    void refreshToken_shouldResponseWithAndOkStatusCode() throws Exception {
//
//        SignupForm signupForm = SignupForm.builder()
//                .firstName("First Name")
//                .lastName("Last Name")
//                .password("123")
//                .email("email@someemail.com")
//                .build();
//        AuthResponseForm authResponseForm = new AuthResponseForm("accessToken");
//        String refreshToken = "refreshToken";
//
//        when(authService.register(any(SignupForm.class), any(HttpServletResponse.class))).thenReturn(authResponseForm);
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setMaxAge(10000);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setSecure(true);
//
//        MockHttpServletResponse response = this.mockMvc.perform(post("/auth/signup")
//                        .cookie(refreshTokenCookie)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(signupForm))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.access_token")
//                        .value(authResponseForm.getAccessToken()))
//                .andReturn().getResponse();
//
//        Cookie cookieValue = response.getCookie("refreshToken");
//        assertThat(cookieValue).isNotNull();
//        assertThat(cookieValue.getValue()).isEqualTo(refreshToken);
//    }

}
