package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmValidationFailedException;
import ru.yandex.practicum.filmorate.exception.UserValidationFailedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class ValidateService {
    public static boolean isValid;

    public static void validateUser(User user) {
        isValid = true;
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            log.info("Электронная почта нового пользователя с id = {} пустая", user.getId());
            isValid = false;
            throw new UserValidationFailedException("Электронная почта пользователя должна быть заполнена");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения нового пользователя с id = {} не верна", user.getId());
            isValid = false;
            throw new UserValidationFailedException("Дата рождения не может быть в будущем");
        }
        if (user.getLogin().contains(" ")) {
            log.info("Логин нового пользователя с id = {} указан не верно", user.getId());
            isValid = false;
            throw new UserValidationFailedException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Имя пользователя с id = {} пустое, вместо него записан логин", user.getId());
            isValid = true;
        }
        if (!user.getEmail().contains("@")) {
            log.info("Электронная нового пользователя с id = {} указана не верно", user.getId());
            isValid = false;
            throw new UserValidationFailedException("Электронная почта должна содержат @");
        }
        isValid = true;
    }

    public static void validateFilm(Film film) {
        isValid = true;
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Название нового фильма с id = {} пустое", film.getId());
            isValid = false;
            throw new FilmValidationFailedException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Описание нового фильма с id = {} слишком длинное", film.getId());
            isValid = false;
            throw new FilmValidationFailedException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза нового фильма с id = {} указана не корректно", film.getId());
            isValid = false;
            throw new FilmValidationFailedException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Продолжительность нового фильма с id = {} указана не корректно", film.getId());
            isValid = false;
            throw new FilmValidationFailedException("Продолжительность фильма должна быть положительной");
        }
        isValid = true;
    }
}
