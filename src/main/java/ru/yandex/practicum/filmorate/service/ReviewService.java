package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedDbStorage feedDbStorage;

    public Review addReview(Review review) {
        Integer filmId = review.getFilmId();
        Integer userId = review.getUserId();
        filmService.get(filmId);
        userService.get(userId);
        Review createdReview = reviewStorage.addReview(review, userId, filmId);
        if (feedDbStorage.addReviewSaveToFeed(createdReview.getReviewId(), userId) == 0) {
            log.warn("'Add Review' operation from user {} to review {} was not saved to Feed",
                    userId, createdReview.getReviewId());
        }
        return createdReview;
    }

    public Review getReview(Integer id) {
        return reviewStorage.getReview(id);
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        Review updatedReview = reviewStorage.getReview(review.getReviewId());
        if (feedDbStorage.updateReviewSaveToFeed(updatedReview.getReviewId(), updatedReview.getUserId()) == 0) {
            log.warn("'Update Review' operation from user {} to review {} was not saved to Feed",
                    updatedReview.getUserId(), updatedReview.getReviewId());
        }
        return updatedReview;
    }

    public void deleteReview(int id) {
        int userId = reviewStorage.deleteReview(id);
        if (feedDbStorage.removeReviewSaveToFeed(id, userId) == 0) {
            log.warn("'Remove Review' operation from user {} to review {} was not saved to Feed", userId, id);
        }
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

    public void deleteLikeReview(int id, int userId) {
        reviewStorage.deleteLikeReview(id, userId);
    }

    public void deleteDislikeReview(int id, int userId) {
        reviewStorage.deleteDislikeReview(id, userId);
    }
}
