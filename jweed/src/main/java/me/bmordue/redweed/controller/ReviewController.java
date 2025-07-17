package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import io.micronaut.http.annotation.Post;
import me.bmordue.redweed.model.dto.IngestHReviewResponseDto;
import me.bmordue.redweed.service.ReviewService;

@Controller("/reviews")
public class ReviewController {

    @Inject
    private ReviewService reviewService;

    @Post
    public IngestHReviewResponseDto ingestHReview(@Body String body) {
        return reviewService.ingestHReview(body);
    }
}
