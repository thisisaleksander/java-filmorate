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

import java.time.LocalDate;
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
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rate, mpa) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa()
        );
        log.info(FILM_LOG, LocalDateTime.now(), "added");
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS ORDER BY id DESC LIMIT 1");
        Film filmToReturn = mapRowToFilm(resultSet);
        return Optional.of(filmToReturn);
    }

    @Override
    public Optional<Film> update(@NonNull Integer id, @NonNull Film film) {
        ValidateService.validateFilm(film);
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa(),
                id
        );
        log.info(FILM_LOG, LocalDateTime.now(), "updated");
        return Optional.of(film);
    }

    @Override
    public Optional<Film> get(@NonNull Integer id) {
        List<Film> filmsList = jdbcTemplate.query("SELECT films.id, films.name, films.description, films.release_date, " +
                        "films.duration, films.rate, " +
                        "genres.genre_id, genres.genre_name, " +
                        "mpa.mpa_id, mpa.mpa_name " +
                        "FROM films " +
                        "LEFT JOIN film_genre on films.id = film_genre.film_id " +
                        "LEFT JOIN genres on film_genre.genre_id = genres.genre_id " +
                        "LEFT JOIN mpa on mpa.mpa_id = films.mpa_id " +
                        "WHERE films.id = " + id,
                new FilmMapper()
        );
        Film film = filmsList.get(0);
        if (film == null) {
            log.info("Film not found, id = {}", id);
            throw new DoNotExistException("Film with id = " + id + " do not exist");
        }
        log.info("Found film with id = {}", id);
        return Optional.of(film);
    }

    public Film mapRowToFilm(SqlRowSet resultSet) {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(LocalDate.parse(Objects.requireNonNull(resultSet.getString("release_date"))))
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .build();
    }

    @Override
    public Set<Film> getAll() {
        // GenreMapper genreMapper = new GenreMapper();
        List<Film> filmsList = jdbcTemplate.query("SELECT films.id, films.name, films.description, films.release_date, " +
                        "films.duration, films.rate, " +
                        "genres.genre_id, genres.genre_name, " +
                        "mpa.mpa_id, mpa.mpa_name " +
                        "FROM films " +
                "LEFT JOIN film_genre on films.id = film_genre.film_id " +
                "LEFT JOIN genres on film_genre.genre_id = genres.genre_id " +
                "LEFT JOIN mpa on mpa.mpa_id = films.mpa_id",
                new FilmMapper()
        );
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
}
