package ru.yandex.practicum.filmorate.exception;

public class UserValidationFailedException extends RuntimeException  {
    public UserValidationFailedException(String message) {
        super(message);
    }
}
