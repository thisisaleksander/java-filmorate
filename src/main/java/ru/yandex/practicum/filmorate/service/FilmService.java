package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_ACTIVE;
import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_DELETED;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * method that adds like from user to a film
     * @param id -> id of a film to add like to
     * @param userId -> id of a user whose like was added
     * @return Optional<Film> -> film object where like was added
     */
    public Optional<Film> addLike(Integer id, Integer userId) throws SQLException {
        Optional<Film> optionalFilm = filmStorage.get(id);
        Optional<User> optionalUser = userStorage.get(userId);
        Film film;
        if (optionalUser.isPresent() && optionalFilm.isPresent()) {
            film = optionalFilm.get();
        } else {
            throw new DoNotExistException(String.format(
                    "Film with id %s or user %s do not exist",
                    id,
                    userId
            ));
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM likes " +
                        "WHERE (film_id = ? AND user_id = ?) " +
                        "AND (status_id = ?)",
                id,
                userId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            throw new AlreadyExistException(String.format(
                    "Like from user id %s to film %s already exist",
                    userId,
                    id
            ));
        } else {
            String sqlQuery = "INSERT INTO likes (film_id, user_id, status_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    userId,
                    STATUS_ACTIVE
            );
            log.info("Add like request from user {} to film {} with status {}", userId, id, STATUS_ACTIVE);
            film.setRate(film.getRate() + 1);
            filmStorage.update(film.getId(), film);
            log.info("Film rate updated");
            return Optional.of(film);
        }
    }

    /**
     * method to delete like in film from user
     * @param id -> id of a film to delete like form
     * @param userId -> id of a user whose like needs to be removed
     * @return Optional<Film> -> film object where like was removed
     */
    public Optional<Film> deleteLike(Integer id, Integer userId) throws SQLException {
        Optional<Film> optionalFilm = filmStorage.get(id);
        Optional<User> optionalUser = userStorage.get(userId);
        Film film;
        if (optionalUser.isPresent() && optionalFilm.isPresent()) {
            film = optionalFilm.get();
        } else {
            throw new DoNotExistException(String.format(
                    "Film with id %s or user %s do not exist",
                    id,
                    userId
            ));
        }
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM likes " +
                        "WHERE (film_id = ? AND user_id = ?) " +
                        "AND (status_id = ?)",
                id,
                userId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            String sqlQuery = "UPDATE likes SET " +
                    "status_id = ?" +
                    "WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    STATUS_DELETED,
                    id,
                    userId
            );
            log.info("Delete like from user {} to film {} with status {}", userId, id, STATUS_DELETED);
            if (film.getRate() == 0) {
                log.info("Film rate updated");
                return Optional.of(film);
            } else {
                film.setRate(film.getRate() - 1);
                filmStorage.update(film.getId(), film);
            }
            log.info("Film rate updated");
            return Optional.of(film);
        } else {
            throw new DoNotExistException(String.format(
                    "Like from user id %s to film %s do not exist",
                    userId,
                    id
            ));
        }
    }

    /**
     * method to get top n films by rate from films table
     * @param count -> amount of films to get, uses in LIMIT statement
     * @return List<Film> -> list of top n films
     */
    public List<Film> getTopFilms(long count) throws SQLException {
        List<Film> films = new ArrayList<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "SELECT DISTINCT * FROM films ORDER BY rate desc LIMIT " + count
        );
        while (resultSet.next()) {
            Film film = filmStorage.mapRowToFilm(resultSet);
            GenreMapper genreMapper = new GenreMapper();
            SqlRowSet genresRowSet = jdbcTemplate.queryForRowSet(
                    "SELECT GENRES.id, genre_name FROM FILM_GENRE " +
                            "LEFT JOIN GENRES ON FILM_GENRE.genre_id = GENRES.id" +
                            " WHERE film_id = " + film.getId()
            );
            while (genresRowSet.next()) {
                film.addGenre(genreMapper.mapRow((ResultSet) genresRowSet, genresRowSet.getRow()));
            }
            films.add(film);
        }
        if (films.isEmpty()) {
            throw new DoNotExistException("No films found in database");
        }
        return films;
    }
}
