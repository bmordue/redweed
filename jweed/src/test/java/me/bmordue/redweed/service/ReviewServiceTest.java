package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.ReviewRepository;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        verify(reviewRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel);

        // Verify the model contains a review resource with the correct type
        Resource reviewType = capturedModel.createResource(RedweedVocab.REV_REVIEW);
        assertTrue(capturedModel.contains(null, RDF.type, reviewType),
                "Model should contain a resource with rdf:type rev:Review");

        // Get the review resource
        Resource reviewResource = capturedModel.listSubjectsWithProperty(RDF.type, reviewType).next();
        assertNotNull(reviewResource);
        assertTrue(reviewResource.getURI().startsWith("http://example.com/reviews/"),
                "Review URI should start with the review namespace");

        // Verify review properties
        Property ratingProperty = capturedModel.createProperty(RedweedVocab.REV_RATING);
        Property textProperty = capturedModel.createProperty(RedweedVocab.REV_TEXT);
        Property reviewerProperty = capturedModel.createProperty(RedweedVocab.REV_REVIEWER);

        Statement ratingStmt = capturedModel.getProperty(reviewResource, ratingProperty);
        assertNotNull(ratingStmt, "Review should have a rating");
        assertEquals("5", ratingStmt.getObject().toString(), "Rating should be '5'");

        Statement textStmt = capturedModel.getProperty(reviewResource, textProperty);
        assertNotNull(textStmt, "Review should have text");
        assertEquals("This is a test review.", textStmt.getObject().toString(), "Text should match expected value");

        Statement reviewerStmt = capturedModel.getProperty(reviewResource, reviewerProperty);
        assertNotNull(reviewerStmt, "Review should have a reviewer");
        assertEquals("John Doe", reviewerStmt.getObject().toString(), "Reviewer should be 'John Doe'");
    }
}
