package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public void addNewFilm(@RequestBody Film film) {
        if(films.containsKey(film.getId())) {
            throw new AlreadyExistException(String.format(
                    "Фильм указанным id [id = %s] уже существует.",
                    film.getId()
            ));
        }
        films.put(film.getId(), film);
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {
        films.put(film.getId(), film);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }
}
