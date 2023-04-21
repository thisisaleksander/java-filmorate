package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    String END_DATE = "9999-12-31";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Film> addLike(Integer id, Integer userId) {
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
                "select * from likes " +
                        "where (film_id = ? and user_id = ?) " +
                        "and (status_id = 2)",
                id,
                userId
        );
        if (resultSet.next()) {
            throw new AlreadyExistException(String.format(
                    "Like from user id %s to film %s already exist",
                    userId,
                    id
            ));
        } else {
            String sqlQuery = "insert into likes (film_id, user_id, status_id, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    userId,
                    2,
                    Instant.now(),
                    LocalDate.parse(END_DATE, formatter)
            );
            log.info("Add like request from user {} to film {} with status {}", userId, id, 2);
            film.setRate(film.getRate() + 1);
            filmStorage.update(film.getId(), film);
            log.info("Film rate updated");
            return Optional.of(film);
        }
    }

    public Optional<Film> deleteLike(Integer id, Integer userId) {
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
                "select * from likes " +
                        "where (film_id = ? and user_id = ?) " +
                        "and (status_id = 2)",
                id,
                userId
        );
        if (resultSet.next()) {
            String sqlQueryOne = "update likes set " +
                    "status_end = ?" +
                    "where (film_id = ? and user_id = ?) " +
                    "and (status_id = 2)";
            jdbcTemplate.update(sqlQueryOne,
                    Instant.now(),
                    id,
                    userId
            );
            String sqlQueryTwo = "insert into likes (film_id, user_id, status_id, status_start, status_end) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQueryTwo,
                    id,
                    userId,
                    3,
                    Instant.now(),
                    LocalDate.parse(END_DATE, formatter)
            );
            log.info("Add like from user {} to film {} with status {}", userId, id, 2);
            film.setRate(film.getRate() - 1);
            filmStorage.update(film.getId(), film);
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

    public List<Film> getTopFilms(long count) {
        List<Film> filmsList = new ArrayList<>();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(
                "select distinct * from films order by rate desc limit" + count
        );
        while (resultSet.next()) {
            filmsList.add(filmStorage.mapRowToFilm(resultSet));
        }
        return filmsList;
    }
}
