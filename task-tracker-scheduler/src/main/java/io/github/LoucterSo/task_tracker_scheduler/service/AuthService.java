package io.github.LoucterSo.task_tracker_scheduler.service;

import io.github.LoucterSo.task_tracker_scheduler.form.auth.AuthResponseForm;
import io.github.LoucterSo.task_tracker_scheduler.form.auth.LoginForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${url.backend}")
    private String backendUrl;
    private final RestTemplate restTemplate;

    public String authenticate(String login, String password) {
        ResponseEntity<AuthResponseForm> authResponse = restTemplate.postForEntity(
                backendUrl + "/api/v1/auth/login",
                new LoginForm(login, password),
                AuthResponseForm.class);

        if (!authResponse.getStatusCode().is2xxSuccessful() || authResponse.getBody() == null)
            throw new RuntimeException("Authentication failed");

        return authResponse.getBody().accessToken();
    }
}
