package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.service.PlaceService;

/**
 * Controller for handling Google Maps data.
 */
@Controller("/maps")
public class GoogleMapsController {

    private final PlaceService placeService;

    /**
     * Constructor.
     *
     * @param placeService the place service
     */
    @Inject
    public GoogleMapsController(PlaceService placeService) {
        this.placeService = placeService;
    }

    /**
     * Import a KML file.
     *
     * @param body the KML file body
     * @return the response
     */
    @Post("/import")
    public IngestKmlResponseDto importKml(@Body String body) {
        return placeService.ingestKml(body);
    }
}
