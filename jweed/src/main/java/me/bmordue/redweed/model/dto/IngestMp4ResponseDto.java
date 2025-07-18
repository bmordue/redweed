package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class IngestMp4ResponseDto {
    private String resourceUri;
    private String message;

    public IngestMp4ResponseDto(String resourceUri, String message) {
        this.resourceUri = resourceUri;
        this.message = message;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
