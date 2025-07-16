package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.domain.Person;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PersonRepository {

    private static final Logger log = LoggerFactory.getLogger(PersonRepository.class);

    @Inject
    private Dataset dataset;

    public void save(Model model) {
        dataset.begin(ReadWrite.WRITE);
        try {
            dataset.getDefaultModel().add(model);
            dataset.commit();
        } catch (Exception e) {
            dataset.abort();
            log.error("Error saving model to dataset", e);
            throw e;
        } finally {
            dataset.end();
        }
    }

    public Person findByUri(String uri) {
        // Logic to find a person by ID in the dataset
        dataset.begin(ReadWrite.READ);
        try {
            String sparql = "SELECT ?s WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> ; <http://example.org/redweed#id> \"" + uri + "\" }";

            try (QueryExecution qexec = QueryExecutionFactory.create(sparql, dataset)) {
                ResultSet results = qexec.execSelect();
                if (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Resource resource = soln.getResource("s");
                    return new Person(resource.getURI());
                }
                return null;
            }
        } finally {
            dataset.end();
        }
    }
}
