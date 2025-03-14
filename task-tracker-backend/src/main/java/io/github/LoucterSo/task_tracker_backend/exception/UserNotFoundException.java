package io.github.LoucterSo.task_tracker_backend.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
