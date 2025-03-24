package io.github.LoucterSo.task_tracker_backend.exception.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Throwable cause) {
        super(cause);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
