package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

@Repository
@Slf4j
public class FeedDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int addLikeSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(film_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 1, 2)";
        log.info("PF-3. Adding a like saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int removeLikeSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(film_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 1, 1)";
        log.info("DF-2. Removing a like saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int addFriendSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(friend_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 3, 2)";
        log.info("PU-3. Adding a friend saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int removeFriendSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(friend_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 3, 1)";
        log.info("DU-2. Removing a friend saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int addReviewSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(review_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 2, 2)";
        log.info("PR-1. Adding a review saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int updateReviewSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(review_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 2, 3)";
        log.info("PR-2. Updating a review saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public int removeReviewSaveToFeed(Integer entityId, Integer userId) {
        String sqlQuery = "insert into feed(review_id, user_id, event_type_id, operation_id) " +
                "values (?, ?, 2, 1)";
        log.info("DR-1. Removing a review saved to the feed");
        return jdbcTemplate.update(sqlQuery, entityId, userId);
    }

    public Collection<Feed> getUsersActionFeed(Integer id) {
        String sqlQuery = "select * from feed f " +
                "join " +
                "event_type et on f.event_type_id = et.id " +
                "join " +
                "operation_type ot on f.operation_id = ot.id " +
                "where user_id = ?";
        log.info("GU-6. Collection of feeds received");
        return jdbcTemplate.query(sqlQuery, new FeedMapper(), id);
    }
}
