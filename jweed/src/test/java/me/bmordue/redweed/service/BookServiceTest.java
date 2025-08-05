package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.vocabulary.BookVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        verify(rdfRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel, "Model should not be null");

        // Verify that the model contains at least one statement
        assertFalse(capturedModel.isEmpty(), "Model should contain RDF statements");

        // Get all resources in the model
        List<Resource> bookResources = capturedModel.listSubjects().toList();
        assertEquals(1, bookResources.size(), "Model should contain exactly one book resource");

        Resource bookResource = bookResources.get(0);

        // Verify the book resource URI starts with the expected namespace
        assertTrue(bookResource.getURI().startsWith("http://example.com/books/"),
                "Book URI should use the configured namespace");

        // Verify the book has the correct RDF type
        assertTrue(capturedModel.contains(bookResource, RDF.type,
                        capturedModel.createResource(BookVocabulary.BIBO_BOOK)),
                "Book should have bibo:Book type");

        // Verify that statements were created (even if metadata is empty from the temp file)
        // At minimum, we should have the type statement
        List<Statement> statements = capturedModel.listStatements().toList();
        assertFalse(statements.isEmpty(), "Model should contain at least the type statement");
    }
}
