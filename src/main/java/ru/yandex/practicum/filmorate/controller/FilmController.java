package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody Film film) {
        filmId++;
        film.setId(filmId);
        if (films.containsKey(film.getId())) {
            throw new AlreadyExistException(String.format(
                    "Фильм указанным id [id = %s] уже существует.",
                    film.getId()
            ));
        }
        ValidateService.validateFilm(film);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм, id = {}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        ValidateService.validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм с id = {}", film.getId());
            return films.get(film.getId());
        } else {
            throw new DoNotExistException(String.format(
                    "Фильм указанным id [id = %s] не существует.",
                    film.getId()
            ));
        }
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Текущее количество постов: {}", films.size());
        return films.values();
    }
}
