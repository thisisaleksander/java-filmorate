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

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Director> findAll() {
        String sqlQuery = "SELECT id, name, deleted FROM directors ORDER BY id";
        log.info("List of all directors received");
        return jdbcTemplate.query(sqlQuery, new DirectorMapper());
    }

    public Director findDirectorById(int id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(
                "SELECT id, name, deleted FROM directors WHERE id = ?", id);
        if (directorRows.next()) {
            log.info("Director {} received", id);
            return new Director(directorRows.getInt("id"), directorRows.getString("name"),
                    directorRows.getBoolean("deleted"));
        } else {
            log.info("Director with id {} not found", id);
            throw new NotFoundException(String.format("Director with id %d not found", id));
        }
    }

    public Director save(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        log.info("Director with id {} saved", director.getId());
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
            log.info("Director with id {} updated", director.getId());
            return director;
        } else {
            log.info("Director with id {} not found", director.getId());
            throw new NotFoundException(String.format("Director with id %d not found", director.getId()));
        }
    }

    public void deleteDirector(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Director {} was removed", id);
    }

    public Set<Director> findDirectorsByFilmId(Integer id) {
        Set<Director> directors = new HashSet<>();
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT id FROM films WHERE id = ?", id);
        if (directorRows.next()) {
            String sqlQuery = "SELECT d.id, d.name, d.deleted " +
                    "FROM directors AS d " +
                    "JOIN film_director AS fd ON fd.director_id = d.id " +
                    "WHERE fd.film_id = ? AND fd.status_id = 2";
            directors.addAll(jdbcTemplate.query(sqlQuery, new DirectorMapper(), id));
            log.info("List of all directors of film with id {} received", id);
            return directors;
        } else {
            log.info(String.format("Film with id %d not found", id));
            throw new NotFoundException(String.format("Film with id %d not found", id));
        }
    }
}