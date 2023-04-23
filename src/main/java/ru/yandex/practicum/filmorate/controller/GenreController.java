package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
        log.info("Received GET request");
        return genreDbStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable("id") Integer id) {
        log.info("Received GET request");
        return genreDbStorage.getGenreById(id);
    }

    @PostMapping
    public Genre addGenre(@RequestBody @Valid Genre genre) {
        log.info("Received POST request");
        return genreDbStorage.addGenre(genre);
    }
}
