package io.github.LoucterSo.task_tracker_backend.form.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TaskForm {
    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;
    private boolean isDone = false;
}
