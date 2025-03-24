package io.github.LoucterSo.task_tracker_scheduler.service;

import io.github.LoucterSo.task_tracker_scheduler.form.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTaskService {
    @Value("${url.backend}")
    private String backendUrl;
    private final RestTemplate restTemplate;

    public List<UserDto> getUsersWithTasks(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<UserDto>> usersResponse = restTemplate.exchange(
                backendUrl + "/api/v1/users",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {});

        if (!usersResponse.getStatusCode().is2xxSuccessful() || usersResponse.getBody() == null) {
            throw new RuntimeException("Failed to fetch users");
        }

        return usersResponse.getBody();
    }
}
