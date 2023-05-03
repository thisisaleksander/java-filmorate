package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review, int userId, int filmId);

    Review getReview(Integer id);

    void updateReview(Review review);

    Review deletedReview(int id);

    List<Review> getAllReviewByFilm(Integer filmId, int count);

    List<Review> getAllReview(int count);

    Review addLikeReview(int id, int userId);

    Review addDislikeReview(int id, int userId);

    Review deletedLikeReview(int id, int userId);

    Review deletedDislikeReview(int id, int userId);
}
