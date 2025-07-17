package me.bmordue.redweed.repository;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RdfRepositoryTest {

    @Mock
    Dataset dataset;

    @InjectMocks
    PersonRepository personRepository;

    @Test
    void testSaveWithException() {
        Model model = ModelFactory.createDefaultModel();
        when(dataset.getDefaultModel()).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> personRepository.save(model));

        verify(dataset).begin(any(org.apache.jena.query.ReadWrite.class));
        verify(dataset).abort();
    }

}