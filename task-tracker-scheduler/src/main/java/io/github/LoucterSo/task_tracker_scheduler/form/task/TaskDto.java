package io.github.LoucterSo.task_tracker_scheduler.form.task;

import java.sql.Timestamp;

public record TaskDto(Long id, String title, String description, boolean isDone, Timestamp completionTime) {

}
