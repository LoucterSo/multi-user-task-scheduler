package io.github.LoucterSo.task_tracker_scheduler.form.user;


import io.github.LoucterSo.task_tracker_scheduler.form.task.TaskDto;

import java.util.Set;

public record UserDto(Long id, String email, String firstName, String lastName, Set<TaskDto> tasks) {
}
