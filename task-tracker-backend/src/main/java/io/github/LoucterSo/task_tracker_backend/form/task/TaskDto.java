package io.github.LoucterSo.task_tracker_backend.form.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

public record TaskDto(
        Long id,
        @NotBlank(message = "Title cannot be empty") String title,
        String description,
        @JsonProperty("done") boolean isDone,
        @JsonProperty("completion_time") Timestamp completionTime
) {

    public static TaskDto fromEntity(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isDone(),
                task.getCompletionTime()
        );
    }
}
