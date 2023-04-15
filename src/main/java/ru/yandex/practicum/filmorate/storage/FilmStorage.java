package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Set;

public interface FilmStorage {
    Film add(Film film);

    Film update(int id, Film film);

    Film get(int id);

    Set<Film> getAll();
}
