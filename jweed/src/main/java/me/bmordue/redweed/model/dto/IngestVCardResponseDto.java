package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of a vCard ingestion.
 */
@Serdeable
public class IngestVCardResponseDto {

    private String personUri;
    private String message;

    /**
     * Constructor.
     *
     * @param message a message
     */
    public IngestVCardResponseDto(String message) {
        this.message = message;
    }

    /**
     * Get the URI of the person.
     *
     * @return the URI of the person
     */
    public String getPersonUri() {
        return personUri;
    }

    /**
     * Set the URI of the person.
     *
     * @param personUri the URI of the person
     */
    public void setPersonUri(String personUri) {
        this.personUri = personUri;
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
