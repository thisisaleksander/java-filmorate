package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("ID"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setFilmId(resultSet.getInt("film_id"));
        review.setUseful(resultSet.getInt("useful"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setDeleted(resultSet.getBoolean("DELETED"));

        String usersLikes = resultSet.getString("users_likes");
        if (usersLikes != null) {
            String[] usersId = usersLikes.split(",");
            for (String userId : usersId) {
                review.getUsersLikes().add(Integer.parseInt(userId));
            }
        }
        String usersDislikes = resultSet.getString("users_dislikes");
        if (usersDislikes != null) {
            String[] usersId = usersDislikes.split(",");
            for (String userId : usersId) {
                review.getUsersDislikes().add(Integer.parseInt(userId));
            }
        }
        return review;
    }
}
