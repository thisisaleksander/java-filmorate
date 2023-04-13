package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film setLike(int id, long userId) {
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
        Map<Integer, Film> map = filmStorage.getFilmsMap();
        List<Film> sortedList = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : map.entrySet()) {
            list.add(entry.getValue().getLikes().size());
        }
        Collections.sort(list);
        for (int num : list) {
            for (Map.Entry<Integer, Film> entry : map.entrySet()) {
                if (entry.getValue().getLikes().size() == num) {
                    sortedList.add(entry.getValue());
                }
            }
        }
        return sortedList;
    }
}
