package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class ControllerErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        log.info("409 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new ApiError(
                HttpStatus.CONFLICT.value(),
                "Integrity constraint has been violated.",
                e.getMessage(),
                sw.toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleConflict(final ForbiddenException e) {
        log.info("403 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                sw.toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final SQLException e) {
        log.info("409 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new ApiError(
                HttpStatus.CONFLICT.value(),
                "Integrity constraint has been violated.",
                e.getMessage(),
                sw.toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "The required object was not found.",
                e.getMessage(),
                sw.toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequestException(final IncorrectRequestException e) {
        log.info("400 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Incorrectly made request.",
                e.getMessage(),
                sw.toString()
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final InternalServerException e, HttpStatus status) {
        log.info("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        String stackTrace = sw.toString();
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error ....", e.getMessage(), stackTrace);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(final ValidationException e) {
        log.info("400 {}", e.getMessage(), e);

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                e.getMessage(),
                stackTrace
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }
}
