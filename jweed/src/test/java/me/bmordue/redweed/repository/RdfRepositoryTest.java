package me.bmordue.redweed.repository;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RdfRepositoryTest {

    @Mock
    Dataset dataset;

    @InjectMocks
    RdfRepository rdfRepository;

    @Test
    void testSaveWithException() {
        Model model = ModelFactory.createDefaultModel();
        when(dataset.getDefaultModel()).thenThrow(new JenaException("Test Exception"));

        assertThrows(JenaException.class, () -> rdfRepository.save(model));

        verify(dataset).begin(any(org.apache.jena.query.ReadWrite.class));
        verify(dataset).abort();
    }
}
