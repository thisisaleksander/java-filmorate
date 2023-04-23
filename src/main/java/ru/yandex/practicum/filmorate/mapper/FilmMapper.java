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
        Set<Genre> filmGenres = new HashSet<>();
        GenreMapper genreMapper = new GenreMapper();
        for (int i = 0; i < resultSet.getArray("genres").getResultSet().getFetchSize(); i++) {
            filmGenres.add(genreMapper.mapRow(resultSet.getArray("genres").getResultSet(), i));
        }
        return new Film(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                LocalDate.parse(Objects.requireNonNull(resultSet.getString("release_date"))),
                resultSet.getInt("duration"),
                resultSet.getInt("rate"),
                new Mpa(
                        resultSet.getInt("mpa_id"),
                        resultSet.getString("mpa_name")
                        ),
                filmGenres
                );
    }
}
