package ru.yandex.practicum.filmorate.exception;

import org.springframework.stereotype.Component;

@Component
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {
        super(message);
    }
}