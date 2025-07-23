package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Inject;
import io.micronaut.http.annotation.Post;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.service.PlaceService;

@Controller("/places")
public class PlaceController {
    private final PlaceService placeService;

    @Inject
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Post
    public IngestKmlResponseDto ingestKml(@Body String body) {
        return placeService.ingestKml(body);
    }
}
