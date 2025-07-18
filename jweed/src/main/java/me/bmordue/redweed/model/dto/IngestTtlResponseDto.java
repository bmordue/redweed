package me.bmordue.redweed.model.dto;

public class IngestTtlResponseDto {

    private String message;

    public IngestTtlResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
