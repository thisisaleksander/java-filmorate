package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_ACTIVE;
import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_DELETED;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage,
                       FeedDbStorage feedDbStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedDbStorage = feedDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * method that adds like from user to a film
     * @param id -> id of a film to add like to
     * @param userId -> id of a user whose like was added
     * @return Film -> film object where like was added
     */
    public Film addLike(Integer id, Integer userId) {
        Film film = filmStorage.get(id);
        userStorage.get(userId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id, status_id) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                userId,
                STATUS_ACTIVE
        );
        log.info("PF-3. Add like request from user {} to film {} with status {}", userId, id, STATUS_ACTIVE);
        film.setRate(film.getRate() + 1);
        filmStorage.update(film.getId(), film);
        log.info("PF-3. Film {} rate updated", id);
        if (feedDbStorage.addLikeSaveToFeed(id, userId) == 0) {
            log.warn("PF-3. 'Add Like' operation from user {} to film {} was not saved to Feed", userId, id);
        }
        return film;
    }

    /**
     * method to delete like in film from user
     * @param id -> id of a film to delete like form
     * @param userId -> id of a user whose like needs to be removed
     * @return Film -> film object where like was removed
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
            log.info("DF-2. Delete like from user {} to film {} with status {}", userId, id, STATUS_DELETED);
            if (film.getRate() == 0) {
                log.info("DF-2. Film {} rate updated", id);
                return film;
            } else {
                film.setRate(film.getRate() - 1);
                filmStorage.update(film.getId(), film);
            }
            log.info("DF-2. Film {} rate updated", id);
            if (feedDbStorage.removeLikeSaveToFeed(id, userId) == 0) {
                log.warn("DF-2. 'Remove Like' operation from user {} to film {} was not saved to Feed", userId, id);
            }
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
     * @param limit -> amount of films to get, uses in LIMIT statement
     * @return List<Film> -> list of top n films
     */
    public List<Film> getMostPopularFilms(Integer count, Integer limit, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilms(count, limit, genreId, year);
    }

    /**
     * method to find films that was liked by users form @params
     * @param userId -> id of a user to get liked films
     * @param friendId -> id of a user to get liked films
     * @return List<Film> -> list of films that was liked by both users
     */
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userStorage.get(userId);
        userStorage.get(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getSortedFilmsWithIdDirector(Integer id, String sortBy) {
        return filmStorage.getSortedFilmsWithIdDirector(id, sortBy);
    }

    public List<Film> getFilmsByKeyWord(String query, String by) {
        return filmStorage.findFilmsByKeyWord(query, by);
    }

    public Film get(@NonNull Integer id) {
        return filmStorage.get(id);
    }
}
