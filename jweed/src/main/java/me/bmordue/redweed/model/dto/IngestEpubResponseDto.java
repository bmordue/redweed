package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of an EPUB ingestion.
 */
@Serdeable
public class IngestEpubResponseDto {
    private String bookUri;
    private String message;

    /**
     * Constructor.
     *
     * @param bookUri the URI of the book
     * @param message a message
     */
    public IngestEpubResponseDto(String bookUri, String message) {
        this.bookUri = bookUri;
        this.message = message;
    }

    /**
     * Get the URI of the book.
     *
     * @return the URI of the book
     */
    public String getBookUri() {
        return bookUri;
    }

    /**
     * Set the URI of the book.
     *
     * @param bookUri the URI of the book
     */
    public void setBookUri(String bookUri) {
        this.bookUri = bookUri;
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
