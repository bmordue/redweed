package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
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
        verify(rdfRepository).save(any(Model.class));
    }
}
