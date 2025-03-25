package io.github.LoucterSo.task_tracker_scheduler.service;

import io.github.LoucterSo.task_tracker_scheduler.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_scheduler.form.auth.LoginForm;
import io.github.LoucterSo.task_tracker_scheduler.form.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
@EnableFeignClients
@RequiredArgsConstructor
public class BackendService {
    private final BackendFeignClient backendFeignClient;

    public String authenticate(String login, String password) {
        ResponseEntity<AuthResponseForm> authResponse =
                backendFeignClient.login(new LoginForm(login, password));

        if (!authResponse.getStatusCode().is2xxSuccessful() || authResponse.getBody() == null)
            throw new RuntimeException("Authentication failed");

        return authResponse.getBody().accessToken();
    }

    public List<UserDto> getUsersWithTasks(String accessToken) {
        ResponseEntity<List<UserDto>> usersResponse =
                backendFeignClient.getUsersWithTasks("Bearer " + accessToken);

        if (!usersResponse.getStatusCode().is2xxSuccessful() || usersResponse.getBody() == null) {
            throw new RuntimeException("Failed to fetch users");
        }

        return usersResponse.getBody();
    }

    @FeignClient("task-tracker-backend")
    interface BackendFeignClient {
        @PostMapping("/api/v1/auth/login")
        ResponseEntity<AuthResponseForm> login(@RequestBody LoginForm loginForm);

        @GetMapping("/api/v1/users")
        ResponseEntity<List<UserDto>> getUsersWithTasks(@RequestHeader("Authorization") String token);
    }

}
