package ru.yandex.practicum.filmorate.exception;

import org.springframework.stereotype.Component;

@Component
public class FilmValidationFailedException extends RuntimeException {
    public FilmValidationFailedException(String message) {
        super(message);
    }
}
