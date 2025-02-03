package io.github.LoucterSo.task_tracker_backend.exception;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationFoundErrorsException extends RuntimeException {

    private final List<FieldError> fields;

    public ValidationFoundErrorsException(String message, List<FieldError> fields) {
        super(message);
        this.fields = new ArrayList<>(fields);
    }

    public ValidationFoundErrorsException(List<FieldError> fields) {
        this.fields = new ArrayList<>(fields);
    }

}
