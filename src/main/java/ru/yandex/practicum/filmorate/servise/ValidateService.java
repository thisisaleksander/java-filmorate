package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmValidationFailedException;
import ru.yandex.practicum.filmorate.exception.UserValidationFailedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class ValidateService {
    public static boolean isValid;
    private static final String USER_LOG = "USER VALIDATION FAILED - {} : ";
    private static final String FILM_LOG = "FILM VALIDATION FAILED - {} : ";

    public static void validateUser(User user) {
        isValid = true;
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            log.info(USER_LOG + "invalid email", LocalDateTime.now());
            isValid = false;
            throw new UserValidationFailedException("Электронная почта пользователя должна быть заполнена");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info(USER_LOG + "invalid birthday", LocalDateTime.now());
            isValid = false;
            throw new UserValidationFailedException("Дата рождения не может быть в будущем");
        }
        if (user.getLogin().contains(" ")) {
            log.info(USER_LOG + "invalid login", LocalDateTime.now());
            isValid = false;
            throw new UserValidationFailedException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info(USER_LOG + "empty name, name = login", LocalDateTime.now());
            isValid = true;
        }
        if (!user.getEmail().contains("@")) {
            log.info(USER_LOG + "invalid email", LocalDateTime.now());
            isValid = false;
            throw new UserValidationFailedException("Электронная почта должна содержат @");
        }
        isValid = true;
    }

    public static void validateFilm(Film film) {
        isValid = true;
        if (film.getName() == null || film.getName().isBlank()) {
            log.info(FILM_LOG + "title is empty", LocalDateTime.now());
            isValid = false;
            throw new FilmValidationFailedException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info(FILM_LOG + "too long description", LocalDateTime.now());
            isValid = false;
            throw new FilmValidationFailedException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info(FILM_LOG + "invalid release date", LocalDateTime.now());
            isValid = false;
            throw new FilmValidationFailedException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info(FILM_LOG + "invalid duration", LocalDateTime.now());
            isValid = false;
            throw new FilmValidationFailedException("Продолжительность фильма должна быть положительной");
        }
        isValid = true;
    }
}
