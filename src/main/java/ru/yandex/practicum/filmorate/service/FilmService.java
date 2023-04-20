package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.*;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final UserDbStorage userStorage;

    public FilmService(InMemoryFilmStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(int id, long userId) {
        Film film = filmStorage.get(id);
        userStorage.get(userId);
        if (film.isAlreadyLikedBy(userId)) {
            throw new AlreadyExistException(String.format(
                    "Film with id = %s already has like from user with id = %s",
                    id,
                    userId
            ));
        } else {
            film.addLike(userId);
            return film;
        }
    }

    public Film deleteLike(int id, long userId) {
        Film film = filmStorage.get(id);
        userStorage.get(userId);
        if (film.isAlreadyLikedBy(userId)) {
            film.removeLike(userId);
            return film;
        } else {
            throw new DoNotExistException(String.format(
                    "Film with id = %s do not have like from user with id = %s",
                    id,
                    userId
            ));
        }
    }

    public List<Film> getTopFilms(int count) {
        List<Film> filmsList = new ArrayList<>(filmStorage.getAll());
        filmsList.sort(Comparator.comparingInt(Film::countLikes));
        if (count >= filmsList.size()) {
            return filmsList;
        } else {
            return filmsList.subList(filmsList.size() - count, filmsList.size());
        }
    }
}
