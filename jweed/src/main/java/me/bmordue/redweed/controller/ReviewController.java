package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import io.micronaut.http.annotation.Post;
import me.bmordue.redweed.model.dto.IngestHReviewResponseDto;
import me.bmordue.redweed.service.ReviewService;

/**
 * Controller for handling reviews.
 */
@Controller("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Constructor.
     *
     * @param reviewService the review service
     */
    @Inject
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Ingest a h-review.
     *
     * @param body the h-review body
     * @return the response
     */
    @Post
    public IngestHReviewResponseDto ingestHReview(@Body String body) {
        return reviewService.ingestHReview(body);
    }
}
