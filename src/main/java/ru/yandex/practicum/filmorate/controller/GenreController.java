package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDbStorage genreDbStorage;

    @GetMapping
    public List<Genre> getAll() {
        log.info("GGen-1. Received GET request: all genres");
        return genreDbStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable("id") Integer id) {
        log.info("GGen-2. Received GET request: genre {}", id);
        return genreDbStorage.getGenreById(id);
    }

    @PostMapping
    public Genre addGenre(@RequestBody @Valid Genre genre) {
        log.info("PGen-1. Received POST request: new genre");
        return genreDbStorage.addGenre(genre);
    }
}
