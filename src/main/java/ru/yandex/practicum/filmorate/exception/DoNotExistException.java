package ru.yandex.practicum.filmorate.exception;

public class DoNotExistException extends RuntimeException {
    public DoNotExistException(String message) {
        super(message);
    }
}