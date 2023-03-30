package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    ValidateService validateService = new ValidateService();
    private int filmId = 0;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewFilm(@RequestBody Film film) {
        filmId++;
        film.setId(filmId);
        if(films.containsKey(film.getId())) {
            throw new AlreadyExistException(String.format(
                    "Фильм указанным id [id = %s] уже существует.",
                    film.getId()
            ));
        }
        validateService.validateFilm(film);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм, id = {}", film.getId());
    }

    @PutMapping
    public void updateFilm(@RequestBody @Valid Film film) {
        validateService.validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм с id = {}", film.getId());
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.debug("Текущее количество постов: {}", films.size());
        return films.values();
    }
}
