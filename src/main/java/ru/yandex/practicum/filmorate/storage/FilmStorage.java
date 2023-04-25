package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.sql.SQLException;
import java.util.Set;

public interface FilmStorage {
    /**
     * @param film -> film object from json
     * @return Optional<Film> -> created film object if exists
     */
    Film add(Film film);

    /**
     * @param id -> id gets from film object
     * @param film -> film object from json
     * @return Optional<Film> -> updated film object if exists
     */
    Film update(Integer id, Film film);

    /**
     * @param id -> parameter from request
     * @return Optional<Film> -> found film with @param id if exists
     */
    Film get(Integer id) throws SQLException;

    /**
     * @return Set<Film> -> all unique films from table 'films'
     */
    Set<Film> getAll() throws SQLException;

    /**
     * method that adds new link to genre, uses table filmGenre
     * @param genreId -> id of genre to add
     * @param filmId -> id of a film to add genre to
     */
    void addGenre(Integer genreId, Integer filmId);

    /**
     * method removes link to genre with genre_id = genreId, uses table filmGenre
     * @param genreId -> id of a genre to remove from film
     * @param filmId -> id of a film to remove genre from
     */
    void removeGenre(Integer genreId, Integer filmId);

    void removeAllGenres(Integer filmId);

    void addMpa(Integer mpaId, Integer filmId);

    void removeMpa(Integer filmId);
}
