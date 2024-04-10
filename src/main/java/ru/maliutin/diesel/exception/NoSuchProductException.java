package ru.maliutin.diesel.exception;

/**
 * Исключение при отсутствии товара.
 */
public class NoSuchProductException extends RuntimeException{
    public NoSuchProductException(String message) {
        super(message);
    }
}
