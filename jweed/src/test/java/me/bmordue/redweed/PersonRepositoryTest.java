package me.bmordue.redweed;

import org.junit.jupiter.api.Test;

import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {

    @Mock
    Dataset dataset;

    @InjectMocks
    PersonRepository personRepository;

    @Test
    void testSave() {
        Model model = ModelFactory.createDefaultModel();
        Model defaultModel = mock(Model.class);
        when(dataset.getDefaultModel()).thenReturn(defaultModel);

        personRepository.save(model);

        verify(dataset).begin(any(org.apache.jena.query.ReadWrite.class));
        verify(dataset).commit();
        verify(defaultModel).add(model);
    }

    @Test
    void testSaveWithException() {
        Model model = ModelFactory.createDefaultModel();
        when(dataset.getDefaultModel()).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> personRepository.save(model));

        verify(dataset).begin(any(org.apache.jena.query.ReadWrite.class));
        verify(dataset).abort();
    }
}
