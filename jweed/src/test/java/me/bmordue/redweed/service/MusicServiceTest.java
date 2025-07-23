package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp3Parser;
import me.bmordue.redweed.vocabulary.MusicVocabulary;
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

        try (MockedStatic<Mp3Parser> mocked = Mockito.mockStatic(Mp3Parser.class)) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("title", "Test Title");
            mocked.when(() -> Mp3Parser.parse(any(File.class))).thenReturn(metadata);

            // When
            musicService.ingestMp3(file);
        }

        // Then
        verify(rdfRepository).save(any(Model.class));
    }
}
