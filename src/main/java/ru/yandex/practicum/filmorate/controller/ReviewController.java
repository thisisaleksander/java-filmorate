package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody @Valid Review review) {
        log.info("Received POST request: new review");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("Received PUT request: update review {}", review.getReviewId());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") int id) {
        log.info("Received DELETE request: delete review {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Integer id) {
        log.info("Received GET request: review {}", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviewByFilm(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") int count) {
        log.info("Received GET request: all review film id{}", filmId);
        return reviewService.getAllReviewByFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLikeReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Received PUT request: add like review {}", id);
        return reviewService.addLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Received PUT request: add dislike review {}", id);
        return reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Received DELETE request: delete like review {}", id);
        reviewService.deleteLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Received DELETE request: delete dislike review {}", id);
        reviewService.deleteDislikeReview(id, userId);
    }
}
