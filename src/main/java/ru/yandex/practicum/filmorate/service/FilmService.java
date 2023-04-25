package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

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
    public Film addLike(Integer id, Integer userId) {
        Film film = filmStorage.get(id);
        userStorage.get(userId);
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
            log.info("Film {} rate updated", id);
            return film;
        }
    }

    /**
     * method to delete like in film from user
     * @param id -> id of a film to delete like form
     * @param userId -> id of a user whose like needs to be removed
     * @return Optional<Film> -> film object where like was removed
     */
    public Film deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.get(id);
        userStorage.get(userId);
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
                log.info("Film {} rate updated", id);
                return film;
            } else {
                film.setRate(film.getRate() - 1);
                filmStorage.update(film.getId(), film);
            }
            log.info("Film {} rate updated", id);
            return film;
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
    public List<Film> getTopFilms(long count) {
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, f.name, description, release_date, duration, rate, " +
                        "fm.MPA_ID, m.NAME as mpa_name FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID " +
                        "LEFT JOIN MPA m ON m.ID = fm.MPA_ID " +
                        "ORDER BY rate DESC " +
                        "LIMIT " + count,
                new FilmMapper(jdbcTemplate)
        );
        if (filmsList.isEmpty()) {
            log.info("No films found in database");
            return filmsList;
        }
        log.info("Total films found in database: " + filmsList.size());
        return filmsList.stream()
                .sorted(Film::getFilmIdToCompare)
                .collect(Collectors.toList());
    }
}
