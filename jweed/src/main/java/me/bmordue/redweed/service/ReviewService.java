package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestHReviewResponseDto;
import me.bmordue.redweed.repository.ReviewRepository;
import me.bmordue.redweed.util.HReviewParser;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for reviews.
 */
@Singleton
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RedweedVocab redweedVocab;

    /**
     * Constructor.
     *
     * @param reviewRepository the review repository
     * @param redweedVocab     the redweed vocabulary
     */
    @Inject
    public ReviewService(ReviewRepository reviewRepository, RedweedVocab redweedVocab) {
        this.reviewRepository = reviewRepository;
        this.redweedVocab = redweedVocab;
    }

    /**
     * Ingest an h-review.
     *
     * @param html the HTML containing the h-review
     * @return the response
     */
    public IngestHReviewResponseDto ingestHReview(String html) {
        List<Map<String, String>> reviews = HReviewParser.parse(html);
        Model model = ModelFactory.createDefaultModel();
        List<String> reviewUris = reviews.stream()
                .map(review -> {
                    String reviewUri = redweedVocab.getReviewNamespace() + UUID.randomUUID();
                    Resource reviewResource = model.createResource(reviewUri)
                            .addProperty(RDF.type, model.createResource(RedweedVocab.REV_REVIEW))
                            .addProperty(model.createProperty(RedweedVocab.REV_RATING), review.get("rating"))
                            .addProperty(model.createProperty(RedweedVocab.REV_TEXT), review.get("description"))
                            .addProperty(model.createProperty(RedweedVocab.REV_REVIEWER), review.get("reviewer"));
                    return reviewUri;
                })
                .collect(Collectors.toList());

        reviewRepository.save(model);

        return new IngestHReviewResponseDto(reviewUris, "hReview ingested successfully");
    }
}
