package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Repository
public class GenreDbStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAll() {
        log.info("GGen-1. List of all genres received");
        return jdbcTemplate.query("SELECT DISTINCT ID AS genre_id, NAME AS genre_name, deleted FROM GENRES",
                new GenreMapper()
        );
    }

    public Genre getGenreById(Integer id) {
        List<Genre> genres = jdbcTemplate.query("SELECT ID AS genre_id, NAME AS genre_name, deleted " +
                        "FROM GENRES WHERE ID = " + id,
                new GenreMapper()
        );
        if (genres.isEmpty()) {
            log.info("GGen-2. Genre with id {} not found", id);
            throw new DoNotExistException("Genre with id = " + id + " do not exists");
        }
        log.info("GGen-2. Genre {} received", id);
        return genres.get(0);
    }

    public Genre addGenre(Genre genre) {
        String sqlQuery = "INSERT INTO GENRES (NAME) VALUES (?)";
        jdbcTemplate.update(sqlQuery,
                genre.getName()
        );
        log.info("PGen-1. Genre with id {} added", genre.getId());
        return genre;
    }

    public Set<Genre> getGenresOfFilm(Integer filmId) {
        List<Genre> genres = jdbcTemplate.query("SELECT DISTINCT fg.GENRE_ID AS GENRE_ID, " +
                        "g.NAME AS GENRE_NAME, g.deleted " +
                        "FROM FILM_GENRE fg " +
                        "LEFT JOIN GENRES g ON g.ID = fg.GENRE_ID " +
                        "WHERE fg.STATUS_ID = 2 AND fg.FILM_ID = " + filmId +
                        " ORDER BY fg.GENRE_ID",
                new GenreMapper()
        );
        log.info("List of all genres of film with id {} received", filmId);
        return genres.stream()
                .sorted(Genre::getGenreIdToCompare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
