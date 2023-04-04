package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final String FILM_LOG = "USER - {} : {}, film id = {}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody Film film) {
        ValidateService.validateFilm(film);
        filmId++;
        film.setId(filmId);
        if (films.containsKey(film.getId())) {
            throw new AlreadyExistException(String.format(
                    "Фильм указанным id [id = %s] уже существует.",
                    film.getId()
            ));
        }
        films.put(film.getId(), film);
        log.info(FILM_LOG, LocalDateTime.now(), "added", film.getId());
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        ValidateService.validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info(FILM_LOG, LocalDateTime.now(), "updated", film.getId());
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
        log.info("FILM - {}, total amount of films: {}", LocalDateTime.now(), films.size());
        return films.values();
    }
}
