package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review, int userId, int filmId);

    Review getReview(Integer id);

    Boolean updateReview(Review review);

    int deleteReview(int id);

    List<Review> getAllReviewByFilm(Integer filmId, int count);

    List<Review> getAllReview(int count);

    Review addLikeReview(int id, int userId);

    Review addDislikeReview(int id, int userId);

    Boolean deleteLikeReview(int id, int userId);

    Boolean deleteDislikeReview(int id, int userId);
}
