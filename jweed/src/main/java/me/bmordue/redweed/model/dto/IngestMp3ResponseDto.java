package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of an MP3 ingestion.
 */
@Serdeable
public class IngestMp3ResponseDto {
    private String workUri;
    private String message;

    /**
     * Constructor.
     *
     * @param workUri the URI of the work
     * @param message a message
     */
    public IngestMp3ResponseDto(String workUri, String message) {
        this.workUri = workUri;
        this.message = message;
    }

    /**
     * Get the URI of the work.
     *
     * @return the URI of the work
     */
    public String getWorkUri() {
        return workUri;
    }

    /**
     * Set the URI of the work.
     *
     * @param workUri the URI of the work
     */
    public void setWorkUri(String workUri) {
        this.workUri = workUri;
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
