package io.github.LoucterSo.task_tracker_backend.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
