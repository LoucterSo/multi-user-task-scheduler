package io.github.LoucterSo.task_tracker_backend.exception.auth;

public class CookiesNotFoundException extends RuntimeException {
    public CookiesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookiesNotFoundException(Throwable cause) {
        super(cause);
    }

    public CookiesNotFoundException(String message) {
        super(message);
    }
}
