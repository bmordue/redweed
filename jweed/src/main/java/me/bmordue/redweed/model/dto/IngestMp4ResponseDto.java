package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of an MP4 ingestion.
 */
@Serdeable
public class IngestMp4ResponseDto {
    private String resourceUri;
    private String message;

    /**
     * Constructor.
     *
     * @param resourceUri the URI of the resource
     * @param message     a message
     */
    public IngestMp4ResponseDto(String resourceUri, String message) {
        this.resourceUri = resourceUri;
        this.message = message;
    }

    /**
     * Get the URI of the resource.
     *
     * @return the URI of the resource
     */
    public String getResourceUri() {
        return resourceUri;
    }

    /**
     * Set the URI of the resource.
     *
     * @param resourceUri the URI of the resource
     */
    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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
