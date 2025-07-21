package me.bmordue.redweed.controller;

import jakarta.inject.Inject;
import me.bmordue.redweed.annotation.WithTestDataset;
import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@WithTestDataset
public class RdfControllerTest {

    @Inject
    private RdfRepository rdfRepository;

    @Inject
    private Dataset dataset;

    @Test
    void testImportTtlFiles() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // Load meal.ttl
        try (InputStream mealStream = getClass().getResourceAsStream("doc/meal.ttl")) {
            if (mealStream == null) {
                throw new IOException("Could not find meal.ttl resource");
            }
            model.read(mealStream, null, "TTL");
        }


        // Load trip.ttl
        try (InputStream tripStream = getClass().getResourceAsStream("doc/trip.ttl")) {
            if (tripStream == null) {
                throw new IOException("Could not find trip.ttl resource");
            }
            model.read(tripStream, null, "TTL");
        }

        rdfRepository.save(model);

        // Assertions for meal.ttl
        Statement mealStatement = ResourceFactory.createStatement(
                ResourceFactory.createResource("http://example.org/meal#Alice"),
                ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/knows"),
                ResourceFactory.createResource("http://example.org/meal#Bob")
        );
        assertTrue(dataset.getDefaultModel().contains(mealStatement), "meal.ttl data not loaded correctly");
    }
}
