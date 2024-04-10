package ru.maliutin.diesel.exception;

import lombok.Getter;

import java.util.List;

/**
 * Исключение при валидации полученных данных.
 */
@Getter
public class ValidationProductException extends RuntimeException {

    private final List<String> messages;

    public ValidationProductException(String message, List<String> messages) {
        super(message);
        this.messages = messages;
    }
}
