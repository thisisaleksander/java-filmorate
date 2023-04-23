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
        //for (int i = 0; i < resultSet.getFetchSize(); i++) {
        //    filmGenres.add(genreMapper.mapRow(resultSet, i));
        //}
        filmGenres.add(genreMapper.mapRow(resultSet, resultSet.getRow()));
        return new Film(
                resultSet.getInt("ID"),
                resultSet.getString("NAME"),
                resultSet.getString("DESCRIPTION"),
                LocalDate.parse(Objects.requireNonNull(resultSet.getString("RELEASE_DATE"))),
                resultSet.getInt("DURATION"),
                resultSet.getInt("RATE"),
                new Mpa(
                        resultSet.getInt("MPA_ID"),
                        resultSet.getString("MPA_NAME")
                        ),
                filmGenres
                );
    }
}
