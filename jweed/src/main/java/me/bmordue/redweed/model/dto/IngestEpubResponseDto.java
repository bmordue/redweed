package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class IngestEpubResponseDto {
    private String bookUri;
    private String message;

    public IngestEpubResponseDto(String bookUri, String message) {
        this.bookUri = bookUri;
        this.message = message;
    }

    public String getBookUri() {
        return bookUri;
    }

    public void setBookUri(String bookUri) {
        this.bookUri = bookUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
