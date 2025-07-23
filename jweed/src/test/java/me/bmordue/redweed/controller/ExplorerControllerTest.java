package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.GraphDTO;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class ExplorerControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    private Dataset dataset;

    private MockedStatic<QueryExecutionFactory> queryExecutionFactoryMockedStatic;

    @BeforeEach
    void setUp() {
        queryExecutionFactoryMockedStatic = Mockito.mockStatic(QueryExecutionFactory.class);
    }

    @AfterEach
    void tearDown() {
        queryExecutionFactoryMockedStatic.close();
    }

    @Test
    void testGetGraph() {
        Model model = ModelFactory.createDefaultModel();
        Resource subject = model.createResource("http://example.org/subject");
        Property predicate = model.createProperty("http://example.org/predicate");
        Resource object = model.createResource("http://example.org/object");
        model.add(subject, predicate, object);

        // When the default model is requested, return our test model
        when(dataset.getDefaultModel()).thenReturn(model);

        // Use the mock dataset for the test
        QueryExecution qe = QueryExecutionFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }", model);
        queryExecutionFactoryMockedStatic.when(() -> QueryExecutionFactory.create(any(org.apache.jena.query.Query.class), any(Dataset.class))).thenReturn(qe);

        HttpRequest<String> request = HttpRequest.GET("/api/graph");
        GraphDTO graph = client.toBlocking().retrieve(request, GraphDTO.class);

        assertEquals(2, graph.nodes().size());
        assertEquals(1, graph.edges().size());
    }

    @MockBean(Dataset.class)
    Dataset dataset() {
        return mock(Dataset.class);
    }
}
