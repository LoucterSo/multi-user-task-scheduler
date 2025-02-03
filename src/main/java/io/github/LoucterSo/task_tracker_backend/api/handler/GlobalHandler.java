package io.github.LoucterSo.task_tracker_backend.api.handler;

import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.form.error.ErrorResponse;
import io.github.LoucterSo.task_tracker_backend.form.error.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExists ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleTaskNotFoundException(TaskNotFoundException ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleValidationHasErrorsException(ValidationFoundErrorsException ex) {

        ValidationErrorResponse error = new ValidationErrorResponse();

        List<FieldError> fieldErrors = ex.getFields();
        List<String> fields = new ArrayList<>();

        for (FieldError f : fieldErrors)
            fields.add("Field %s and value %s: %s"
                    .formatted(f.getField(), f.getRejectedValue(), f.getDefaultMessage()));
        error.setErrorFields(fields);
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(exception =
            {
                    RefreshTokenNotFoundException.class,
                    AuthenticationFailedException.class
            })
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleUnexpectedServerException(UnexpectedServerException ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
