package me.bmordue.redweed.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class RdfRepositoryTest {

    @Inject
    private Dataset dataset;

    @Inject
    private RdfRepository rdfRepository;

    @Test
    void save() {
        StringReader reader = new StringReader("""
                @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
                @prefix redweed: <http://bmordue.me/redweed/> .
                
                redweed:test a rdfs:Class .
                """);

        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, reader, "", Lang.TURTLE);

        rdfRepository.save(model);

        dataset.executeRead(() -> {
            Model defaultModel = dataset.getDefaultModel();
            Resource resource = defaultModel.getResource("http://bmordue.me/redweed/test");
            assertTrue(defaultModel.contains(resource, RDF.type, RDFS.Class), "The saved resource should be in the dataset");
        });
    }
}
