package ru.yandex.practicum.filmorate.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        String sqlQuery = "insert into films (name, description, release_date, duration, rate, rating) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getRating()
        );
        log.info(FILM_LOG, LocalDateTime.now(), "added");
        return Optional.of(film);
    }

    @Override
    public Optional<Film> update(@NonNull Integer id, @NonNull Film film) {
        ValidateService.validateFilm(film);
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, rating = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getRating(),
                id
        );
        log.info(FILM_LOG, LocalDateTime.now(), "updated");
        return Optional.of(film);
    }

    @Override
    public Optional<Film> get(@NonNull Integer id) throws SQLException {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        if(resultSet.next()) {
            Film film = mapRowToFilm(resultSet);
            GenreMapper genreMapper = new GenreMapper();
            SqlRowSet genresRowSet = jdbcTemplate.queryForRowSet(
                    "SELECT g.ID , g.GENRE FROM FILM_GENRE fg LEFT JOIN GENRES g ON fg.GENRE_ID = g.ID WHERE fg.FILM_ID = " +
                            film.getId()
            );
            while (genresRowSet.next()) {
                film.setGenres(genreMapper.mapRow((ResultSet) genresRowSet, genresRowSet.getRow()));
            }
            log.info("Found film with id = {}", film.getId());
            return Optional.of(film);
        } else {
            log.info("Film with id = {} not found.", id);
            return Optional.empty();
        }
    }

    public Film mapRowToFilm(SqlRowSet resultSet) {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(LocalDate.parse(Objects.requireNonNull(resultSet.getString("release_date"))))
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .rating(resultSet.getString("rating"))
                .build();
    }

    @Override
    public Set<Film> getAll() throws SQLException {
        Set<Film> films = new HashSet<>();
        GenreMapper genreMapper = new GenreMapper();
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("select * from films");
        if(resultSet.next()) {
            while (resultSet.next()) {
                Film film = mapRowToFilm(resultSet);
                SqlRowSet genresRowSet = jdbcTemplate.queryForRowSet(
                        "SELECT GENRES.id, GENRES.genre_name FROM FILM_GENRE WHERE film_id = " + film.getId() +
                                "LEFT JOIN GENRES ON FILM_GENRE.genre_id = GENRES.id"
                );
                while (genresRowSet.next()) {
                    film.setGenres(genreMapper.mapRow((ResultSet) genresRowSet, genresRowSet.getRow()));
                }
                films.add(film);
            }
            log.info("Total films found: {}", films.size());
            return films;
        } else {
            log.info("No films found");
            return Collections.emptySet();
        }
    }

    @Override
    public Optional<Film> addGenre(Integer id) {
        return Optional.empty();
    }
}
