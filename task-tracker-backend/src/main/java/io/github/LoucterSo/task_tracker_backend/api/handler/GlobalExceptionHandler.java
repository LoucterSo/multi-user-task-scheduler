package io.github.LoucterSo.task_tracker_backend.api.handler;

import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.auth.RefreshTokenNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.task.TaskNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserAlreadyExists;
import io.github.LoucterSo.task_tracker_backend.form.error.ErrorResponse;
import io.github.LoucterSo.task_tracker_backend.form.error.ValidationErrorResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice @Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExists ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException.class)
    public ErrorResponse handleNotFoundException(Exception ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ValidationErrorResponse handleValidationHasErrorsException(ValidationFoundErrorsException ex) {

        List<FieldError> fieldErrors = ex.getFields();
        List<String> fields = new ArrayList<>();

        for (FieldError f : fieldErrors)
            fields.add("Field %s and value %s: %s"
                    .formatted(f.getField(), f.getRejectedValue(), f.getDefaultMessage()));

        return new ValidationErrorResponse(fields, System.currentTimeMillis());
    }

    @ExceptionHandler(exception =
            {
                    JwtException.class,
                    RefreshTokenNotFoundException.class,
                    AuthenticationFailedException.class
            })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(), System.currentTimeMillis()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleUnexpectedServerException(UnexpectedServerException ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }
}
