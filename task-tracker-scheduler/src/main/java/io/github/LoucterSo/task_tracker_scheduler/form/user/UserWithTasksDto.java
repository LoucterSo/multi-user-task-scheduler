package io.github.LoucterSo.task_tracker_scheduler.form.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.LoucterSo.task_tracker_scheduler.form.task.TaskDto;

import java.util.Set;

public record UserWithTasksDto(Long id, String email, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName, Set<TaskDto> tasks) {
}
