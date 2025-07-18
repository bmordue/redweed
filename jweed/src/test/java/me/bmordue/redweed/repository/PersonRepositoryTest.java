package me.bmordue.redweed.repository;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
