package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.PlaceRepository;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private RedweedVocab redweedVocab;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void ingestKml() {
        // Given
        String kml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <kml xmlns="http://www.opengis.net/kml/2.2">
              <Document>
                <Placemark>
                  <name>Test Placemark</name>
                  <description>Test Description</description>
                  <Point>
                    <coordinates>-122.0822035425683,37.42228990140251,0</coordinates>
                  </Point>
                </Placemark>
              </Document>
            </kml>
            """;

        when(redweedVocab.getPlaceNamespace()).thenReturn("http://example.com/places/");

        // When
        placeService.ingestKml(kml);

        // Then
        verify(placeRepository).save(any(Model.class));
    }
}
