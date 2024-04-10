package ru.maliutin.diesel.exception;

/**
 * Исключение некорректного пути.
 */
public class IncorrectUriException extends RuntimeException{
    public IncorrectUriException(String message) {
        super(message);
    }
}
