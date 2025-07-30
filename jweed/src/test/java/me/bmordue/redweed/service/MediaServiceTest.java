package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp4Parser;
import me.bmordue.redweed.vocabulary.MediaVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RdfRepository rdfRepository;

    @Mock
    private MediaVocabulary mediaVocabulary;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void ingestMp4() throws IOException {
        // Given
        File file = File.createTempFile("test", ".mp4");
        file.deleteOnExit();

        when(mediaVocabulary.getResourceNamespace()).thenReturn("http://example.com/media/");

        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);

        try (MockedStatic<Mp4Parser> mocked = Mockito.mockStatic(Mp4Parser.class)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", "Test Title");
            mocked.when(() -> Mp4Parser.parse(any(File.class))).thenReturn(metadata);

            // When
            mediaService.ingestMp4(file, "test://test.mp4");
        }

        // Then
        verify(rdfRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel);

        // Verify the model contains exactly one resource
        List<Resource> subjects = capturedModel.listSubjects().toList();
        assertEquals(1, subjects.size(), "Model should contain exactly one subject");
        Resource mediaResource = subjects.get(0);


        // Verify the resource has the correct type
        Statement typeStatement = capturedModel.getProperty(mediaResource, RDF.type);
        assertNotNull(typeStatement);
        assertEquals(MediaVocabulary.MA_MEDIA_RESOURCE, typeStatement.getObject().toString());

        // Verify the resource has the correct title
        Statement titleStatement = capturedModel.getProperty(mediaResource, capturedModel.createProperty(MediaVocabulary.MA_TITLE));
        assertNotNull(titleStatement);
        assertEquals("Test Title", titleStatement.getObject().toString());

        // Verify the resource URI starts with the expected namespace
        assertTrue(mediaResource.getURI().startsWith("http://example.com/media/"));
    }
}
