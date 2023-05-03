package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        Integer filmId = review.getFilmId();
        Integer userId = review.getUserId();
        try {
            filmStorage.get(filmId);
        } catch (RuntimeException | SQLException e) {
            throw new NotFoundException("Film id " + filmId + " not found");
        }
        try {
            userStorage.get(userId);
        } catch (RuntimeException | SQLException ee) {
            throw new NotFoundException("User id " + userId + " not found");
        }
        return reviewStorage.addReview(review, userId, filmId);
    }

    public Review getReview(Integer id) {
        return reviewStorage.getReview(id);
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        return reviewStorage.getReview(review.getReviewId());
    }

    public Review deletedReview(int id) {
        return reviewStorage.deletedReview(id);
    }

    public List<Review> getAllReviewByFilm(Integer filmId, int count) {
        if (filmId != null) {
            return reviewStorage.getAllReviewByFilm(filmId, count);
        } else {
            return reviewStorage.getAllReview(count);
        }
    }

    public Review addLikeReview(int id, int userId) {
        return reviewStorage.addLikeReview(id, userId);
    }

    public Review addDislikeReview(int id, int userId) {
        return reviewStorage.addDislikeReview(id, userId);
    }

    public Review deletedLikeReview(int id, int userId) {
        return reviewStorage.deletedLikeReview(id, userId);
    }

    public Review deletedDislikeReview(int id, int userId) {
        return reviewStorage.deletedDislikeReview(id, userId);
    }
}
