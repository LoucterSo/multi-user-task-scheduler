package io.github.LoucterSo.task_tracker_backend.exception.auth;

public class AuthorizationFailedException extends RuntimeException {

    public AuthorizationFailedException(Throwable cause) {
        super(cause);
    }

    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationFailedException(String message) {
        super(message);
    }
}
