package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.service.PlaceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
class PlaceControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    private PlaceService placeService;

    @Test
    void testIngestKml() {
        String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...";

        when(placeService.ingestKml(kml)).thenReturn(new IngestKmlResponseDto(java.util.Collections.emptyList(), ""));

        HttpRequest<String> request = HttpRequest.POST("/places", kml);
        HttpResponse<IngestKmlResponseDto> response = client.toBlocking().exchange(request, IngestKmlResponseDto.class);
        assertEquals(io.micronaut.http.HttpStatus.OK, response.getStatus());

        verify(placeService, times(1)).ingestKml(kml);
    }

    @MockBean(PlaceService.class)
    PlaceService placeService() {
        return mock(PlaceService.class);
    }
}
