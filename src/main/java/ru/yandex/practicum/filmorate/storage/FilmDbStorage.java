package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.time.LocalDateTime;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_ACTIVE;
import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_DELETED;

@Slf4j
@Component
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String FILM_LOG = "FILM - {} : {}";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> add(@NonNull Film film) {
        ValidateService.validateFilm(film);
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rate) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate()
        );
        log.info(FILM_LOG, LocalDateTime.now(), "added");
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, name, description, release_date, duration, rate, " +
                        "MPA_ID, GENRE_ID FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID  " +
                        "LEFT JOIN (SELECT * FROM FILM_GENRE WHERE status_id = 2) fg ON f.ID = fg.FILM_ID " +
                        "ORDER BY f.ID DESC LIMIT 1",
                new FilmMapper()
        );
        Film filmToReturn = filmsList.get(0);
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            addMpa(film.getMpa().getId(), filmToReturn.getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> addGenre(genre.getId(), filmToReturn.getId()));
        }
        filmsList = jdbcTemplate.query("SELECT f.ID, name, description, release_date, duration, rate , MPA_ID, GENRE_ID FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID  " +
                        "LEFT JOIN (SELECT * FROM FILM_GENRE WHERE status_id = 2) fg ON f.ID = fg.FILM_ID " +
                        "ORDER BY f.ID DESC LIMIT 1",
                new FilmMapper()
        );
        return Optional.of(filmsList.get(0));
    }

    @Override
    public Optional<Film> update(@NonNull Integer id, @NonNull Film film) {
        get(id);
        ValidateService.validateFilm(film);
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                id
        );
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            addMpa(film.getMpa().getId(), film.getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> addGenre(genre.getId(), film.getId()));
        }
        log.info(FILM_LOG, LocalDateTime.now(), "updated");
        return Optional.of(film);
    }

    @Override
    public Optional<Film> get(@NonNull Integer id) {
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, name, description, release_date, duration, rate , MPA_ID, GENRE_ID FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID  " +
                        "LEFT JOIN (SELECT * FROM FILM_GENRE WHERE status_id = 2) fg ON f.ID = fg.FILM_ID " +
                        "WHERE f.ID = " + id,
                new FilmMapper()
        );
        if (filmsList.isEmpty()) {
            log.info("Film not found, id = {}", id);
            throw new DoNotExistException("Film with id = " + id + " do not exist");
        }
        log.info("Found film with id = {}", id);
        Film film = filmsList.get(0);
        return Optional.of(film);
    }

    @Override
    public Set<Film> getAll() {
        List<Film> filmsList = jdbcTemplate.query("SELECT f.ID, name, description, release_date, duration, rate , MPA_ID, GENRE_ID FROM films f " +
                        "LEFT JOIN (SELECT * FROM FILM_MPA WHERE status_id = 2) fm ON f.ID = fm.FILM_ID  " +
                        "LEFT JOIN (SELECT * FROM FILM_GENRE WHERE status_id = 2) fg ON f.ID = fg.FILM_ID ",
                new FilmMapper()
        );
        if (filmsList.isEmpty()) {
            log.info("No films found in database");
        }
        log.info("Total films found: {}", filmsList.size());
        return new HashSet<>(filmsList);
    }

    @Override
    public void addGenre(Integer genreId, Integer filmId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_genre WHERE (film_id = ? AND genre_id = ?) AND status_id = ?",
                filmId,
                genreId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            log.info("Genre already added to film with id = {}", filmId);
        } else {
            String sqlQuery = "INSERT INTO film_genres (film_id, genre_id, status_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    genreId,
                    STATUS_ACTIVE
            );
            log.info("Add new genre to film with id = {}", filmId);
        }
    }

    @Override
    public void removeGenre(Integer genreId, Integer filmId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM film_genre WHERE (film_id = ? AND genre_id = ?) AND status_id = ?",
                filmId,
                genreId,
                STATUS_ACTIVE
        );
        if (resultSet.next()) {
            String sqlQuery = "UPDATE film_genres SET status_id = ? WHERE (film_id = ? AND genre_id = ?)";
            jdbcTemplate.update(sqlQuery,
                    STATUS_DELETED,
                    filmId,
                    genreId
            );
            log.info("Genre was removed from film with id = {}", filmId);
        } else {
            log.info("Genre was not added to film with id = {}", filmId);
        }
    }

    @Override
    public void addMpa(Integer mpaId, Integer filmId) {
        removeMpa(filmId);
        String sqlQuery = "INSERT INTO film_mpa (film_id, mpa_id, status_id) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                mpaId,
                STATUS_ACTIVE
        );
        log.info("Add new mpa to film with id = {}", filmId);
    }

    @Override
    public void removeMpa(Integer filmId) {
        String sqlQuery = "UPDATE film_mpa SET status_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                STATUS_DELETED,
                filmId
        );
        log.info("Mpa was removed from film with id = {}", filmId);
    }
}
