package ru.practicum.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private Exception exception;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }

}