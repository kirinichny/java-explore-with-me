package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        final String reason = "Запрос составлен некорректно.";
        return createErrorResponse(e, HttpStatus.BAD_REQUEST.name(), reason);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        final String reason = "Требуемый объект не был найден.";
        return createErrorResponse(e, HttpStatus.NOT_FOUND.name(), reason);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        final String reason = "Нарушение целостности данных.";
        return createErrorResponse(e, HttpStatus.CONFLICT.name(), reason);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final RuntimeException e) {
        final String reason = "Внутренняя ошибка сервера.";
        return createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.name(), reason);
    }

    private ErrorResponse createErrorResponse(Throwable e, String status, String reason) {
        log.error("Получен статус {}: {}", status, e.getMessage());

        LocalDateTime currentDateTime = LocalDateTime.now();

        return ErrorResponse.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason(reason)
                .status(status)
                .timestamp(currentDateTime)
                .build();
    }
}