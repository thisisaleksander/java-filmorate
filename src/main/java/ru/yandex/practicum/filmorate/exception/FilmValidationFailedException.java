package ru.yandex.practicum.filmorate.exception;

public class FilmValidationFailedException extends RuntimeException {
    public FilmValidationFailedException(String message) {
        super(message);
    }
}
