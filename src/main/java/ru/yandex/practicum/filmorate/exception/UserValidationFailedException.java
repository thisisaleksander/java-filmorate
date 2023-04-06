package ru.yandex.practicum.filmorate.exception;

import org.springframework.stereotype.Component;

@Component
public class UserValidationFailedException extends RuntimeException  {
    public UserValidationFailedException(String message) {
        super(message);
    }
}
