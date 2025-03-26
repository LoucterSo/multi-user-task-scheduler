package io.github.LoucterSo.task_tracker_backend.form.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.LoucterSo.task_tracker_backend.entity.user.User;

public record UserWithoutTasks(
        Long id,
        String email, @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
    public static UserWithoutTasks fromEntity(User user) {
        return new UserWithoutTasks(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}

