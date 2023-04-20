package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Optional<Film> add(Film film);

    Optional<Film> update(int id, Film film);

    Optional<Film> get(int id);

    Set<Film> getAll();
}
