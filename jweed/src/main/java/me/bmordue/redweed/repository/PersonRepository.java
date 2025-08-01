package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.domain.Person;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository for people.
 */
@Singleton
public class PersonRepository extends RdfRepository {

    private static final Logger log = LoggerFactory.getLogger(PersonRepository.class);

    /**
     * Constructor.
     *
     * @param dataset the dataset
     */
    @Inject
    public PersonRepository(Dataset dataset) {
        super(dataset);
    }

    /**
     * Find a person by URI.
     *
     * @param uri the URI of the person
     * @return the person, or null if not found
     */
    public Person findByUri(String uri) {
        // Logic to find a person by ID in the dataset
        dataset.begin(ReadWrite.READ);
        ParameterizedSparqlString pss = new ParameterizedSparqlString(
                "SELECT ?s WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> ; <http://bmordue.me/redweed/id> ?uri . }"
        );
        pss.setLiteral("uri", uri);

        try (QueryExecution qexec = QueryExecutionFactory.create(pss.asQuery(), dataset)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                Resource resource = solution.getResource("s");
                return new Person(resource.getURI());
            }
            return null;
        } finally {
            dataset.end();
        }
    }
}
