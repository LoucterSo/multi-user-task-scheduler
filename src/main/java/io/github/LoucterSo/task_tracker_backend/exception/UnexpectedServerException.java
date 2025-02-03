package io.github.LoucterSo.task_tracker_backend.exception;

public class UnexpectedServerException extends RuntimeException {
    public UnexpectedServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedServerException(Throwable cause) {
        super(cause);
    }

    public UnexpectedServerException(String message) {
        super(message);
    }
}
