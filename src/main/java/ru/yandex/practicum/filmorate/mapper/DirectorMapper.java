package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}