package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TtlServiceTest {

    @Mock
    private RdfRepository rdfRepository;

    @InjectMocks
    private TtlService ttlService;

    @Test
    void ingestTtl() {
        // Given
        String ttl = """
                @prefix : <http://example.org/> .
                @prefix foaf: <http://xmlns.com/foaf/0.1/> .
                
                :a foaf:name "Test" .
                """;

        // When
        ttlService.ingestTtl(ttl);

        // Then
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        verify(rdfRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();

        // Validate that the model contains the expected resource
        Resource subject = capturedModel.getResource("http://example.org/a");
        assertTrue(capturedModel.containsResource(subject));

        // Validate that the foaf:name property exists with the correct value
        assertTrue(capturedModel.contains(
                subject,
                capturedModel.getProperty("http://xmlns.com/foaf/0.1/name"),
                capturedModel.createLiteral("Test")
        ));
    }
}
