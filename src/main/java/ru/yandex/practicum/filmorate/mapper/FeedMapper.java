package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int resultSetEntityId = resultSet.getInt("film_id");

        if (resultSetEntityId == 0) {
            resultSetEntityId = resultSet.getInt("review_id");
        }
        if (resultSetEntityId == 0) {
            resultSetEntityId = resultSet.getInt("friend_id");
        }

        return Feed.builder()
                .eventId(resultSet.getInt("event_id"))
                .timestamp(resultSet.getTimestamp("created_at").getTime())
                .entityId(resultSetEntityId)
                .userId(resultSet.getInt("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_name")))
                .operation(OperationType.valueOf(resultSet.getString("operation_name")))
                .build();
    }
}
