package ru.yandex.practicum.filmorate.exception;

import org.springframework.stereotype.Component;

@Component
public class DoNotExistException extends RuntimeException {
    public DoNotExistException(String message) {
        super(message);
    }
}