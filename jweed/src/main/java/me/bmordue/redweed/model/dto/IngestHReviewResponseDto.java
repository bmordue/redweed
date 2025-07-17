package me.bmordue.redweed.model.dto;

import java.util.List;

public class IngestHReviewResponseDto {
    private List<String> reviewUris;
    private String message;

    public IngestHReviewResponseDto(List<String> reviewUris, String message) {
        this.reviewUris = reviewUris;
        this.message = message;
    }

    public List<String> getReviewUris() {
        return reviewUris;
    }

    public void setReviewUris(List<String> reviewUris) {
        this.reviewUris = reviewUris;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
