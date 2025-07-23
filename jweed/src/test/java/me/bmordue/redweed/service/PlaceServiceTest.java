package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.PlaceRepository;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        verify(placeRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel, "Captured model should not be null");
        assertFalse(capturedModel.isEmpty(), "Captured model should not be empty");

        // Check for placemark-related statements
        boolean hasPlacemarkData = capturedModel.listStatements().hasNext(stmt ->
                stmt.getObject().toString().contains("Test Placemark") ||
                        stmt.getObject().toString().contains("Test Description") ||
                        stmt.getObject().toString().contains("-122.0822035425683") ||
                        stmt.getObject().toString().contains("37.42228990140251")
        );

        assertTrue(hasPlacemarkData, "Model should contain data from the KML placemark (name, description, or coordinates)");
    }
}
