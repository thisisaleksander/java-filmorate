package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return jdbcTemplate.query("SELECT ID AS genre_id, NAME AS genre_name FROM GENRES",
                new GenreMapper()
        );
    }

    public Genre getGenreById(Integer id) {
        List<Genre> genres = jdbcTemplate.query("SELECT ID AS genre_id, NAME AS genre_name FROM GENRES WHERE ID = " +
                id,
                new GenreMapper()
        );
        if (genres.isEmpty()) {
            throw new DoNotExistException("Genre with id = " + id + " do not exists");
        }
        return genres.get(0);
    }

    public Genre addGenre(Genre genre) {
        String sqlQuery = "INSERT INTO GENRES (NAME) VALUES (?)";
        jdbcTemplate.update(sqlQuery,
                genre.getName()
        );
        return genre;
    }

    public Set<Genre> getGenresOfFilm(Integer filmId) {
        List<Genre> genres = jdbcTemplate.query("SELECT fg.GENRE_ID AS GENRE_ID, g.NAME AS GENRE_NAME " +
                        "FROM FILM_GENRE fg " +
                        "LEFT JOIN GENRES g ON g.ID = fg.GENRE_ID " +
                        "WHERE fg.STATUS_ID = 2 AND fg.FILM_ID = " + filmId,
                new GenreMapper()
        );
        return new HashSet<>(genres);
    }
}
