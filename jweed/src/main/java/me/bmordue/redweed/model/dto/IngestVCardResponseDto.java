package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class IngestVCardResponseDto {

    private String personUri;
    private String message;

    public IngestVCardResponseDto(String personUri, String message) {
        this.personUri = personUri;
        this.message = message;
    }

    public String getPersonUri() {
        return personUri;
    }

    public void setPersonUri(String personUri) {
        this.personUri = personUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
