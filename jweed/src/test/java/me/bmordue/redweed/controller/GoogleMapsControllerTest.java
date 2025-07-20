package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.annotation.WithTestDataset;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithTestDataset
@MicronautTest
public class GoogleMapsControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @Disabled
    void testImportKml() {
        String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "  <Document>\n" +
                "    <name>My Saved Places</name>\n" +
                "    <Placemark>\n" +
                "      <name>Googleplex</name>\n" +
                "      <description>Google's headquarters.</description>\n" +
                "      <Point>\n" +
                "        <coordinates>-122.084,37.422,0</coordinates>\n" +
                "      </Point>\n" +
                "    </Placemark>\n" +
                "  </Document>\n" +
                "</kml>";
        IngestKmlResponseDto response = client.toBlocking().retrieve(HttpRequest.POST("/maps/import", kml), IngestKmlResponseDto.class);
        assertEquals(1, response.getPlaceUris().size());
    }
}
