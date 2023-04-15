package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.exception.FilmValidationFailedException;
import ru.yandex.practicum.filmorate.exception.UserValidationFailedException;
import ru.yandex.practicum.filmorate.model.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFilmValidationFailedException(final FilmValidationFailedException e) {
        return new ErrorResponse(
                String.format("Error in film validation \"%s\".", e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserValidationFailedException(final UserValidationFailedException e) {
        return new ErrorResponse(
                String.format("Error in user validation \"%s\".", e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDoNotExistException(final DoNotExistException e) {
        return new ErrorResponse(
                String.format("Object do not exist \"%s\".", e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.IM_USED)
    public ErrorResponse handleAlreadyExistException(final AlreadyExistException e) {
        return new ErrorResponse(
                String.format("Object do not exist \"%s\".", e.getMessage())
        );
    }
}
