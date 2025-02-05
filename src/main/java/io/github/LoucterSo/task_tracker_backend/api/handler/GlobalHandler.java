package io.github.LoucterSo.task_tracker_backend.api.handler;

import io.github.LoucterSo.task_tracker_backend.exception.*;
import io.github.LoucterSo.task_tracker_backend.form.error.ErrorResponse;
import io.github.LoucterSo.task_tracker_backend.form.error.ValidationErrorResponse;
import io.github.LoucterSo.task_tracker_backend.service.jwt.JwtServiceImpl;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalHandler.class);


    //????
    @ExceptionHandler
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExists ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception ex) {

        ErrorResponse error = new ErrorResponse();

        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
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
                    JwtException.class,
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
