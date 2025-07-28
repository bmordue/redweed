package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * A data transfer object for the response of a KML ingestion.
 */
@Serdeable
public class IngestKmlResponseDto {
    private List<String> placeUris;
    private String message;

    /**
     * Constructor.
     *
     * @param placeUris the URIs of the places
     * @param message   a message
     */
    public IngestKmlResponseDto(List<String> placeUris, String message) {
        this.placeUris = placeUris;
        this.message = message;
    }

    /**
     * Get the URIs of the places.
     *
     * @return the URIs of the places
     */
    public List<String> getPlaceUris() {
        return placeUris;
    }

    /**
     * Set the URIs of the places.
     *
     * @param placeUris the URIs of the places
     */
    public void setPlaceUris(List<String> placeUris) {
        this.placeUris = placeUris;
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
