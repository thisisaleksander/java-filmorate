package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Optional<Film> add(Film film);

    Optional<Film> update(long id, Film film);

    Optional<Film> get(long id);

    Set<Film> getAll();
}
