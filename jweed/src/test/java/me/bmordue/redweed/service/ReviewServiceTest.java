package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.ReviewRepository;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RedweedVocab redweedVocab;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void ingestHReview() {
        // Given
        String html = """
            <div class="hreview">
              <p class="rating">5</p>
              <p class="description">This is a test review.</p>
              <p class="reviewer">John Doe</p>
            </div>
            """;

        when(redweedVocab.getReviewNamespace()).thenReturn("http://example.com/reviews/");

        // When
        reviewService.ingestHReview(html);

        // Then
        verify(reviewRepository).save(any(Model.class));
    }
}
