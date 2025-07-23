package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.service.PlaceService;

@Controller("/maps")
public class GoogleMapsController {

    private final PlaceService placeService;

    @Inject
    public GoogleMapsController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Post("/import")
    public IngestKmlResponseDto importKml(@Body String body) {
        return placeService.ingestKml(body);
    }
}
