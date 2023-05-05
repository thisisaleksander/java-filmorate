package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Director> findAll() {
        String sqlQuery = "SELECT id, name FROM directors ORDER BY id";
        return jdbcTemplate.query(sqlQuery, new DirectorMapper());
    }

    public Director findDirectorById(int id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(
                "SELECT id, name FROM directors WHERE id = ?", id);
        if (directorRows.next()) {
            return new Director(directorRows.getInt("id"), directorRows.getString("name"));
        } else {
            throw new NotFoundException(String.format("Режиссер c id %d не найден", id));
        }
    }

    public Director save(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return director;
    }

    public Director update(Director director) {
        String sqlQuery = "SELECT id FROM directors WHERE id = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlQuery, director.getId());
        if (directorRows.next()) {
            sqlQuery = "UPDATE directors " +
                    "SET name = ? " +
                    "WHERE id = ? ";
            jdbcTemplate.update(sqlQuery,
                    director.getName(),
                    director.getId());
            return director;
        } else {
            throw new NotFoundException(String.format("Режиссер c id %d не найден", director.getId()));
        }
    }

    public void deleteDirector(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Director {} was removed", id);
    }
}