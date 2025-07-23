package me.bmordue.redweed.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.domain.Person;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class PersonRepositoryTest {

    @Inject
    private Dataset dataset;

    @Inject
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        StringReader reader = new StringReader("""
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix foaf: <http://xmlns.com/foaf/0.1/> .
            @prefix redweed: <http://example.org/redweed#> .

            <http://example.org/person/1> rdf:type foaf:Person ;
                redweed:id "1" .
            """);

        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, reader, "", Lang.TURTLE);
        personRepository.save(model);
    }

    @Test
    void findByUri() {
        Person person = personRepository.findByUri("1");
        assertNotNull(person);
        assertEquals("http://example.org/person/1", person.getUri());
    }
}
