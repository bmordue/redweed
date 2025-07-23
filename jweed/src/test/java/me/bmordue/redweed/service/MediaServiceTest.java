package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp4Parser;
import me.bmordue.redweed.vocabulary.MediaVocabulary;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        try (MockedStatic<Mp4Parser> mocked = Mockito.mockStatic(Mp4Parser.class)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", "Test Title");
            mocked.when(() -> Mp4Parser.parse(any(File.class))).thenReturn(metadata);

            // When
            mediaService.ingestMp4(file);
        }

        // Then
        verify(rdfRepository).save(any(Model.class));
    }
}
