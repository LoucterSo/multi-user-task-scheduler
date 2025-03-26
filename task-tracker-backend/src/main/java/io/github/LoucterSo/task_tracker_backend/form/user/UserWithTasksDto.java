package io.github.LoucterSo.task_tracker_backend.form.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record UserWithTasksDto(Long id, String email, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName, Set<TaskDto> tasks) {

    public static UserWithTasksDto fromEntity(User user) {
        return new UserWithTasksDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getTasks() == null ? new HashSet<>() : user.getTasks().stream()
                        .map(TaskDto::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
