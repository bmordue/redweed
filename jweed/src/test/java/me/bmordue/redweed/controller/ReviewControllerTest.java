package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestHReviewResponseDto;
import me.bmordue.redweed.service.ReviewService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@MicronautTest
class ReviewControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    private ReviewService reviewService;

    @Test
    void testIngestHReview() {
        String hreview = "<div class=\"hreview\">...";

        when(reviewService.ingestHReview(hreview)).thenReturn(new IngestHReviewResponseDto(java.util.Collections.emptyList(), ""));

        HttpRequest<String> request = HttpRequest.POST("/reviews", hreview);
        client.toBlocking().retrieve(request);

        verify(reviewService, times(1)).ingestHReview(hreview);
    }

    @MockBean(ReviewService.class)
    ReviewService reviewService() {
        return mock(ReviewService.class);
    }
}
