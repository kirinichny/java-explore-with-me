package ru.practicum.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private StackTraceElement[] errors;
    private String message;
    private String status;
    private LocalDateTime timestamp;
}