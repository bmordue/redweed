package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class IngestVCardResponseDto {

    private String message;

    public IngestVCardResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
