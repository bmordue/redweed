package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestHReviewResponseDto;
import me.bmordue.redweed.repository.ReviewRepository;
import me.bmordue.redweed.util.HReviewParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class ReviewService {

    @Inject
    private ReviewRepository reviewRepository;

    public IngestHReviewResponseDto ingestHReview(String html) {
        List<Map<String, String>> reviews = HReviewParser.parse(html);
        Model model = ModelFactory.createDefaultModel();
        List<String> reviewUris = reviews.stream()
                .map(review -> {
                    String reviewUri = "http://redweed.local/review/" + UUID.randomUUID();
                    Resource reviewResource = model.createResource(reviewUri)
                            .addProperty(RDF.type, model.createResource("http://purl.org/stuff/rev#Review"))
                            .addProperty(model.createProperty("http://purl.org/stuff/rev#rating"), review.get("rating"))
                            .addProperty(model.createProperty("http://purl.org/stuff/rev#text"), review.get("description"))
                            .addProperty(model.createProperty("http://purl.org/stuff/rev#reviewer"), review.get("reviewer"));
                    return reviewUri;
                })
                .collect(Collectors.toList());

        reviewRepository.save(model);

        return new IngestHReviewResponseDto(reviewUris, "hReview ingested successfully");
    }
}
