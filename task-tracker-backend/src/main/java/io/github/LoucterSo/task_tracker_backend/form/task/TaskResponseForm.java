package io.github.LoucterSo.task_tracker_backend.form.task;

import io.github.LoucterSo.task_tracker_backend.entity.task.Task;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
public class TaskResponseForm {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private boolean isDone;
    private Timestamp created;
    private Timestamp updated;
    private Timestamp completionTime;

    public TaskResponseForm(Task task) {
        setId(task.getId());
        setUserId(task.getUser().getId());
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setDone(task.isDone());
        setCreated(task.getCreated());
        setUpdated(task.getUpdated());
        setCompletionTime(task.getCompletionTime());
    }
}
