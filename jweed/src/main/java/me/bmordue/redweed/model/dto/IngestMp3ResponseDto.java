package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class IngestMp3ResponseDto {
    private String workUri;
    private String message;

    public IngestMp3ResponseDto(String workUri, String message) {
        this.workUri = workUri;
        this.message = message;
    }

    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
