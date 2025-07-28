package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * A data transfer object for the response of an h-review ingestion.
 */
@Serdeable
public class IngestHReviewResponseDto {
    private List<String> reviewUris;
    private String message;

    /**
     * Constructor.
     *
     * @param reviewUris the URIs of the reviews
     * @param message    a message
     */
    public IngestHReviewResponseDto(List<String> reviewUris, String message) {
        this.reviewUris = reviewUris;
        this.message = message;
    }

    /**
     * Get the URIs of the reviews.
     *
     * @return the URIs of the reviews
     */
    public List<String> getReviewUris() {
        return reviewUris;
    }

    /**
     * Set the URIs of the reviews.
     *
     * @param reviewUris the URIs of the reviews
     */
    public void setReviewUris(List<String> reviewUris) {
        this.reviewUris = reviewUris;
    }

    /**
     * Get the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
