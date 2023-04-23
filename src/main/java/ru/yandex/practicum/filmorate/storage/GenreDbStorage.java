package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM GENRES", new GenreMapper());
    }

    public Genre getGenreById(Integer id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM GENRES WHERE ID = " + id, new GenreMapper());
        if (genres.isEmpty()) {
            throw new DoNotExistException("Genre with id = " + id + " do not exists");
        }
        return genres.get(0);
    }

    public Genre addGenre(Genre genre) {
        String sqlQuery = "INSERT INTO GENRES (name) " +
                "VALUES (?)";
        jdbcTemplate.update(sqlQuery,
                genre.getName()
        );
        return genre;
    }
}
