package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_ACTIVE;
import static ru.yandex.practicum.filmorate.storage.Constants.STATUS_DELETED;

@Slf4j
@Component
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Review addReview(Review review, int userId, int filmId) {
        String sqlQuery = "SELECT* FROM reviews WHERE film_id = ? AND user_id = ? AND deleted = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId, false);
        if (!reviewRows.next()) {
            jdbcTemplate.update("INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                    "VALUES (?, ?, ?, ?)", review.getContent(), review.getIsPositive(), userId, filmId);
            log.info("Добавлен новый отзыв к фильму " + filmId);
            SqlRowSet reviewRows2 = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId, false);
            if (reviewRows2.next()) {
                review.setReviewId(reviewRows2.getInt("id"));
            }
            return review;
        } else {
            log.info("review to film " + filmId + " already exist");
            throw new AlreadyExistException("review to film " + filmId + " already exist");
        }
    }

    @Override
    public Review getReview(Integer id) {
        try {
            String sqlQuery = "SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id, r.deleted, " +
                    "GROUP_CONCAT(rl.user_id) users_likes, GROUP_CONCAT(rd.user_id) users_dislikes, " +
                    "COUNT(rl.review_id) - COUNT(rd.review_id) useful " +
                    "FROM REVIEWS r " +
                    "LEFT JOIN (SELECT* FROM REVIEW_LIKES WHERE status_id = ?) rl ON rl.review_id = r.id " +
                    "LEFT JOIN (SELECT* FROM REVIEW_DISLIKES WHERE status_id = ?) rd ON rd.review_id = r.id " +
                    "WHERE r.id = ? AND r.deleted = ? " +
                    "GROUP BY r.id";
            return jdbcTemplate.queryForObject(sqlQuery, new ReviewMapper(), STATUS_ACTIVE, STATUS_ACTIVE, id, false);
        } catch (Exception e) {
            throw new NotFoundException("Review id " + id + " not found");
        }
    }

    @Override
    public void updateReview(Review review) {
        Integer reviewId = review.getReviewId();
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT* FROM reviews WHERE id = ? AND deleted = ?",
                reviewId, false);
        if (reviewRows.next()) {
            jdbcTemplate.update("UPDATE reviews SET content = ?, is_positive = ? WHERE id = ? ",
                    review.getContent(), review.getIsPositive(), reviewId);
            log.info("Update review id: {}", reviewId);
        } else {
            log.info("Review id " + reviewId + " not found");
            throw new NotFoundException("Review id " + reviewId + " not found");
        }
    }

    @Override
    public Review deletedReview(int id) {
        String sqlQuery = "SELECT* FROM reviews WHERE id = ? AND deleted = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, false);
        if (reviewRows.next()) {
            jdbcTemplate.update("UPDATE reviews SET deleted = ? WHERE id = ? ",
                    true, id);
            log.info("Deleted review id: {}", id);
            String sqlQuery2 = "SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id, r.deleted, " +
                    "GROUP_CONCAT(rl.user_id) users_likes, GROUP_CONCAT(rd.user_id) users_dislikes, " +
                    "COUNT(rl.review_id) - COUNT(rd.review_id) useful " +
                    "FROM REVIEWS r " +
                    "LEFT JOIN (SELECT* FROM REVIEW_LIKES WHERE status_id = ?) rl ON rl.review_id = r.id " +
                    "LEFT JOIN (SELECT* FROM REVIEW_DISLIKES WHERE status_id = ?) rd ON rd.review_id = r.id " +
                    "WHERE r.id = ? AND r.deleted = ? " +
                    "GROUP BY r.id";
            return jdbcTemplate.queryForObject(sqlQuery2, new ReviewMapper(), STATUS_ACTIVE, STATUS_ACTIVE, id, true);
        } else {
            log.info("Review " + id + " not found");
            throw new NotFoundException("Review " + id + " not found");
        }
    }

    @Override
    public List<Review> getAllReviewByFilm(Integer filmId, int count) {
        List<Review> listReview;
        String sqlQuery = "SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id, r.deleted, " +
                "GROUP_CONCAT(rl.user_id) users_likes, GROUP_CONCAT(rd.user_id) users_dislikes, " +
                "COUNT(rl.review_id) - COUNT(rd.review_id) useful " +
                "FROM REVIEWS r " +
                "LEFT JOIN (SELECT* FROM REVIEW_LIKES WHERE status_id = ?) rl ON rl.review_id = r.id " +
                "LEFT JOIN (SELECT* FROM REVIEW_DISLIKES WHERE status_id = ?) rd ON rd.review_id = r.id " +
                "WHERE r.film_id = ? AND r.deleted = ? " +
                "GROUP BY r.id " +
                "ORDER BY useful DESC, r.id ASC " +
                "LIMIT ?";

        listReview = jdbcTemplate.query(sqlQuery, new ReviewMapper(), STATUS_ACTIVE, STATUS_ACTIVE, filmId,
                false, count);

        return listReview;
    }

    @Override
    public List<Review> getAllReview(int count) {
        List<Review> listReview;
        String sqlQuery = "SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id, r.deleted, " +
                "GROUP_CONCAT(rl.user_id) users_likes, GROUP_CONCAT(rd.user_id) users_dislikes, " +
                "COUNT(rl.review_id) - COUNT(rd.review_id) useful " +
                "FROM REVIEWS r " +
                "LEFT JOIN (SELECT* FROM REVIEW_LIKES WHERE status_id = ?) rl ON rl.review_id = r.id " +
                "LEFT JOIN (SELECT* FROM REVIEW_DISLIKES WHERE status_id = ?) rd ON rd.review_id = r.id " +
                "WHERE r.deleted = ? " +
                "GROUP BY r.id " +
                "ORDER BY useful DESC, r.id ASC " +
                "LIMIT ?";

        listReview = jdbcTemplate.query(sqlQuery, new ReviewMapper(), STATUS_ACTIVE, STATUS_ACTIVE, false, count);

        return listReview;
    }

    @Override
    public Review addLikeReview(int id, int userId) {
        String sqlQuery = "SELECT* FROM REVIEW_LIKES WHERE review_id = ? AND user_id = ? AND status_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, STATUS_ACTIVE);
        if (!reviewRows.next()) {
            jdbcTemplate.update("INSERT INTO REVIEW_LIKES (review_id, user_id, status_id) " +
                    "VALUES (?, ?, ?)", id, userId, STATUS_ACTIVE);
            log.info("Add new like to review " + id);
            return getReview(id);
        } else {
            log.info("like to review " + id + " already exist");
            throw new AlreadyExistException("like to review " + id + " already exist");
        }
    }

    @Override
    public Review addDislikeReview(int id, int userId) {
        String sqlQuery = "SELECT* FROM REVIEW_DISLIKES WHERE review_id = ? AND user_id = ? AND status_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, STATUS_ACTIVE);
        if (!reviewRows.next()) {
            jdbcTemplate.update("INSERT INTO REVIEW_DISLIKES (review_id, user_id, status_id) " +
                    "VALUES (?, ?, ?)", id, userId, STATUS_ACTIVE);
            log.info("Add new dislike to review " + id);
            return getReview(id);
        } else {
            log.info("dislike to review " + id + " already exist");
            throw new AlreadyExistException("dislike to review " + id + " already exist");
        }
    }

    @Override
    public Review deletedLikeReview(int id, int userId) {
        String sqlQuery = "SELECT* FROM REVIEW_LIKES WHERE review_id = ? AND user_id = ? AND status_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, STATUS_ACTIVE);
        if (reviewRows.next()) {
            jdbcTemplate.update("UPDATE REVIEW_LIKES SET status_id = ? WHERE review_id = ? AND user_id = ?",
                    STATUS_DELETED, id, userId);
            log.info("Deleted like to review " + id);
            return getReview(id);
        } else {
            log.info("like to review " + id + " not found");
            throw new NotFoundException("like to review " + id + " not found");
        }
    }

    @Override
    public Review deletedDislikeReview(int id, int userId) {
        String sqlQuery = "SELECT* FROM REVIEW_DISLIKES WHERE review_id = ? AND user_id = ? AND status_id = ?";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, STATUS_ACTIVE);
        if (reviewRows.next()) {
            jdbcTemplate.update("UPDATE REVIEW_DISLIKES SET status_id = ? WHERE review_id = ? AND user_id = ?",
                    STATUS_DELETED, id, userId);
            log.info("Deleted dislike to review " + id);
            return getReview(id);
        } else {
            log.info("dislike to review " + id + " not found");
            throw new NotFoundException("dislike to review " + id + " not found");
        }
    }
}
