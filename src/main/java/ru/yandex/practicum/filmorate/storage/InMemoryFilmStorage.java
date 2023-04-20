package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final String FILM_LOG = "USER - {} : {}, film id = {}";

    public Film add(@NonNull Film film) {
        ValidateService.validateFilm(film);
        filmId++;
        film.setFilmId(filmId);
        if (films.containsKey(film.getFilmId())) {
            throw new AlreadyExistException(String.format(
                    "Film with id = %s already exists",
                    film.getFilmId()
            ));
        }
        films.put(film.getFilmId(), film);
        log.info(FILM_LOG, LocalDateTime.now(), "added", film.getFilmId());
        return films.get(film.getFilmId());
    }

    public Film update(@NonNull int id,@NonNull Film film) {
        ValidateService.validateFilm(film);
        if (films.containsKey(film.getFilmId())) {
            films.put(film.getFilmId(), film);
            log.info(FILM_LOG, LocalDateTime.now(), "updated", film.getFilmId());
            return films.get(film.getFilmId());
        } else {
            throw new DoNotExistException(String.format(
                    "Film with id = %s do not exists",
                    film.getFilmId()
            ));
        }
    }

    public Film get(@NonNull int id) {
        if (!films.containsKey(id)) {
            throw new DoNotExistException(String.format(
                    "Film with id = %s do not exists",
                    id
            ));
        }
        log.info(FILM_LOG, LocalDateTime.now(), "get method", id);
        return films.get(id);
    }

    public Set<Film> getAll() {
        log.info("FILM - {}, total amount of films: {}", LocalDateTime.now(), films.size());
        Set<Film> setOfFilms = new HashSet<>();
        films.forEach((key, value) -> setOfFilms.add(value));
        return setOfFilms;
    }

    public Map<Integer, Film> getFilmsMap() {
        return films;
    }
}
