package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FeedDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("event_id"))
                .timestamp(resultSet.getTimestamp("timestamp").toInstant())
                .entityId(resultSet.getInt("entity_id"))
                .userId(resultSet.getInt("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type_id")))
                .operation(OperationType.valueOf(resultSet.getString("operation_id")))
                .build();
    }
}
