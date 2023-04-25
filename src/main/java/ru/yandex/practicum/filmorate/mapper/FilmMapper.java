package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        Integer filmId = resultSet.getInt("ID");
        Set<Genre> filmGenres = genreDbStorage.getGenresOfFilm(filmId);
        return new Film(
                filmId,
                resultSet.getString("NAME"),
                resultSet.getString("DESCRIPTION"),
                LocalDate.parse(Objects.requireNonNull(resultSet.getString("RELEASE_DATE"))),
                resultSet.getInt("DURATION"),
                resultSet.getInt("RATE"),
                resultSet.getBoolean("DELETED"),
                new Mpa(
                        resultSet.getInt("MPA_ID"),
                        resultSet.getString("MPA_NAME")
                        ),
                filmGenres
                );
    }
}
