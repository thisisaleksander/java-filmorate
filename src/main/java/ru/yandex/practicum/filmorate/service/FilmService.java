package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film setLike(int id, long userId) {
        Film film = filmStorage.get(id);
        if (film.getLikes().contains(userId)) {
            throw new AlreadyExistException(String.format(
                    "Film with id = %s already has like from user with id = %s",
                    id,
                    userId
            ));
        }
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(int id, long userId) {
        Film film = filmStorage.get(id);
        if (!film.getLikes().contains(userId)) {
            throw new DoNotExistException(String.format(
                    "Film with id = %s do not have like from user with id = %s",
                    id,
                    userId
            ));
        }
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedList = new ArrayList<>(filmStorage.getAll());
        return sortedList.stream()
                .sorted(Film::compareByLikes)
                .collect(Collectors.toList());
    }
}
