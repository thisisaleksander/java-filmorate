package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody @Valid Film film) {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmStorage.update(film.getId(), film);
    }

    @GetMapping
    public Set<Film> findAllFilms() {
        return filmStorage.getAll();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable("id") Integer id, @PathVariable("userId") long userId) {
        return filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getTopFilms(count);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer id) {
        return filmStorage.get(id);
    }
}
