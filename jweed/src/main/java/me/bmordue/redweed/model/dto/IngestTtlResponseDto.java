package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of a TTL ingestion.
 */
@Serdeable
public class IngestTtlResponseDto {

    private String message;

    /**
     * Constructor.
     *
     * @param message a message
     */
    public IngestTtlResponseDto(String message) {
        this.message = message;
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
