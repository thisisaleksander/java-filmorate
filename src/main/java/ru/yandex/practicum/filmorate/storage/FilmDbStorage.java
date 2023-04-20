package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private static final String FILM_LOG = "FILM - {} : {}, film id = {}";

    @Override
    public Optional<Film> add(@NonNull Film film) {
        ValidateService.validateFilm(film);
        log.info(FILM_LOG, LocalDateTime.now(), "added", film.getFilmId());
        return Optional.empty();
    }

    @Override
    public Optional<Film> update(@NonNull int id, @NonNull Film film) {
        ValidateService.validateFilm(film);
        log.info(FILM_LOG, LocalDateTime.now(), "updated", film.getFilmId());
        return Optional.empty();
    }

    @Override
    public Optional<Film> get(@NonNull int id) {
        return Optional.empty();
    }

    @Override
    public Set<Film> getAll() {
        return Collections.emptySet();
    }
}
