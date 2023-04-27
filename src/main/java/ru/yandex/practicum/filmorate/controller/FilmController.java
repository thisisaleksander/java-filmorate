package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody @Valid Film film) {
        log.info("Received GET request: new film");
        return filmStorage.add(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Received PUT request: update film {}", film.getId());
        return filmStorage.update(film.getId(), film);
    }

    @GetMapping
    public Set<Film> findAllFilms() {
        log.info("Received GET request: all films");
        return filmStorage.getAll();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Received PUT request: add like to film {} from user {}", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Received DELETE request: remove like of film {} form user {}", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Received GET request: top {} popular films", count);
        return filmService.getTopFilms(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer id) {
        log.info("Received GET request: film {}", id);
        return filmStorage.get(id);
    }
}
