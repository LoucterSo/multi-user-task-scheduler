package io.github.LoucterSo.task_tracker_backend.api.handler;

import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.auth.AuthorizationFailedException;
import io.github.LoucterSo.task_tracker_backend.exception.auth.RefreshTokenNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.task.TaskNotFoundException;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserAlreadyExists;
import io.github.LoucterSo.task_tracker_backend.exception.user.UserNotFoundException;
import io.github.LoucterSo.task_tracker_backend.form.error.ErrorResponse;
import io.github.LoucterSo.task_tracker_backend.form.error.ValidationErrorResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

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
    @ExceptionHandler
    public ErrorResponse handleTaskNotFoundException(TaskNotFoundException ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handleValidationHasErrorsException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ValidationErrorResponse(errors, System.currentTimeMillis());
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
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationFailedException.class)
    public ErrorResponse handleAuthorizationException(AuthorizationFailedException ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleUnexpectedServerException(UnexpectedServerException ex) {

        return new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
    }
}
