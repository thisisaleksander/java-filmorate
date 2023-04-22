package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    /**
     * @param film -> film object from json
     * @return Optional<Film> -> created film object if exists
     */
    Optional<Film> add(Film film);

    /**
     * @param id -> id gets from film object
     * @param film -> film object from json
     * @return Optional<Film> -> updated film object if exists
     */
    Optional<Film> update(Integer id, Film film);

    /**
     * @param id -> parameter from request
     * @return Optional<Film> -> found film with @param id if exists
     */
    Optional<Film> get(Integer id);

    /**
     * @return Set<Film> -> all unique films from table 'films'
     */
    Set<Film> getAll();
}
