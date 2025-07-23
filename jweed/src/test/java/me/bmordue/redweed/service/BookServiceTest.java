package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.vocabulary.BookVocabulary;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private RdfRepository rdfRepository;

    @Mock
    private BookVocabulary bookVocabulary;

    @InjectMocks
    private BookService bookService;

    @Test
    void ingestEpub() throws IOException {
        // Given
        File file = File.createTempFile("test", ".epub");
        file.deleteOnExit();

        when(bookVocabulary.getBookNamespace()).thenReturn("http://example.com/books/");

        // When
        bookService.ingestEpub(file);

        // Then
        verify(rdfRepository).save(any(Model.class));
    }
}
