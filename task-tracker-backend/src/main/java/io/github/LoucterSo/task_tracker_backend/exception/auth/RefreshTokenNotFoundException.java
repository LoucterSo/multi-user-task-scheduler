package io.github.LoucterSo.task_tracker_backend.exception.auth;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenNotFoundException(Throwable cause) {
        super(cause);
    }

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
