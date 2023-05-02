package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        directorService.save(director);
        log.info("Director had been created: {}", director);
        return director;
    }

    @PutMapping()
    public Director updateFilm(@RequestBody @Valid Director director) {
        directorService.update(director);
        log.info("Director had been updated: {}", director);
        return director;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
        log.info("Director {} had been deleted", id);
    }
}
