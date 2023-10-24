package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return createErrorResponse(e, HttpStatus.BAD_REQUEST.name());
    }

    private ErrorResponse createErrorResponse(Throwable e, String status) {
        log.error("Получен статус {}: {}", status, e.getMessage());

        LocalDateTime currentDateTime = LocalDateTime.now();

        return ErrorResponse.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .status(status)
                .timestamp(currentDateTime)
                .build();
    }
}