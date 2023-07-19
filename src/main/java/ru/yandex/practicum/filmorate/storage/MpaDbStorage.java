package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DoNotExistException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAll() {
        log.info("GM-1. List of all MPA received");
        return jdbcTemplate.query("SELECT DISTINCT ID AS mpa_id, NAME AS mpa_name, deleted FROM MPA",
                new MpaMapper()
        );
    }

    public Mpa getMpaById(Integer id) {
        List<Mpa> mpa = jdbcTemplate.query("SELECT ID AS mpa_id, NAME AS mpa_name, deleted FROM MPA WHERE ID = "
                        + id,
                new MpaMapper()
        );
        if (mpa.isEmpty()) {
            log.info("GM-2. MPA with id {} not found", id);
            throw new DoNotExistException("MPA with id = " + id + " do not exists");
        }
        log.info("GM-2. MPA {} received", id);
        return mpa.get(0);
    }
}
