package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDbStorage mpaDbStorage;

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Received GET request: all mpa");
        return mpaDbStorage.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable("id") Integer id) {
        log.info("Received GET request: mpa {}", id);
        return mpaDbStorage.getMpaById(id);
    }
}
