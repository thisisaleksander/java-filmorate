package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(
                resultSet.getInt("ID"),
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
                new HashSet<>()
                );
    }

    public Film mapRow(ResultSet resultSet, Set<Genre> genres) throws SQLException {
        return new Film(
                resultSet.getInt("ID"),
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
                genres
        );
    }
}
