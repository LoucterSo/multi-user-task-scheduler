package io.github.LoucterSo.task_tracker_backend.form.user;

import io.github.LoucterSo.task_tracker_backend.entity.user.User;
import io.github.LoucterSo.task_tracker_backend.form.task.TaskDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record UserDto(Long id, String email, String firstName, String lastName, Set<TaskDto> tasks) {

    public static UserDto fromEntity(User user) {
        return new UserDto(
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
