package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public class IngestKmlResponseDto {
    private List<String> placeUris;
    private String message;

    public IngestKmlResponseDto(List<String> placeUris, String message) {
        this.placeUris = placeUris;
        this.message = message;
    }

    public List<String> getPlaceUris() {
        return placeUris;
    }

    public void setPlaceUris(List<String> placeUris) {
        this.placeUris = placeUris;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
