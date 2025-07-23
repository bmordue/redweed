package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp3Parser;
import me.bmordue.redweed.vocabulary.MusicVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicServiceTest {

    @Mock
    private RdfRepository rdfRepository;

    @Mock
    private MusicVocabulary musicVocabulary;

    @InjectMocks
    private MusicService musicService;

    @Test
    void ingestMp3() throws IOException {
        // Given
        File file = File.createTempFile("test", ".mp3");
        file.deleteOnExit();

        when(musicVocabulary.getWorkNamespace()).thenReturn("http://example.com/music/");

        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);

        try (MockedStatic<Mp3Parser> mocked = Mockito.mockStatic(Mp3Parser.class)) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("title", "Test Title");
            metadata.put("artist", "Test Artist");
            metadata.put("album", "Test Album");
            metadata.put("track", "1");
            metadata.put("genre", "Rock");
            mocked.when(() -> Mp3Parser.parse(any(File.class))).thenReturn(metadata);

            // When
            musicService.ingestMp3(file);
        }

        // Then
        verify(rdfRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel);

        // Verify the model contains exactly one subject (the musical work)
        StmtIterator subjects = capturedModel.listStatements();
        assertTrue(subjects.hasNext(), "Model should contain statements");

        // Find the musical work resource
        Resource workResource = null;
        StmtIterator typeStatements = capturedModel.listStatements(null, RDF.type, capturedModel.createResource(MusicVocabulary.MO_MUSICAL_WORK));
        if (typeStatements.hasNext()) {
            workResource = typeStatements.next().getSubject();
        }
        assertNotNull(workResource, "Model should contain a musical work resource");

        // Verify the work URI starts with the expected namespace
        assertTrue(workResource.getURI().startsWith("http://example.com/music/"),
                "Work URI should start with the configured namespace");

        // Verify RDF type is MusicalWork
        assertTrue(capturedModel.contains(workResource, RDF.type, capturedModel.createResource(MusicVocabulary.MO_MUSICAL_WORK)),
                "Work should have type MusicalWork");

        // Verify metadata properties
        Property titleProperty = capturedModel.createProperty(MusicVocabulary.MO_TITLE);
        assertTrue(capturedModel.contains(workResource, titleProperty, "Test Title"),
                "Work should have title property");

        Property artistProperty = capturedModel.createProperty(MusicVocabulary.MO_ARTIST);
        assertTrue(capturedModel.contains(workResource, artistProperty, "Test Artist"),
                "Work should have artist property");

        Property albumProperty = capturedModel.createProperty(MusicVocabulary.MO_ALBUM);
        assertTrue(capturedModel.contains(workResource, albumProperty, "Test Album"),
                "Work should have album property");

        Property trackProperty = capturedModel.createProperty(MusicVocabulary.MO_TRACK_NUMBER);
        assertTrue(capturedModel.contains(workResource, trackProperty, "1"),
                "Work should have track number property");

        Property genreProperty = capturedModel.createProperty(MusicVocabulary.MO_GENRE);
        assertTrue(capturedModel.contains(workResource, genreProperty, "Rock"),
                "Work should have genre property");
    }
}
