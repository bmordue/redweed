package me.bmordue.redweed.repository;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.domain.Person;

@Singleton
public class PersonRepository {
    
    @Inject
    private Dataset dataset;

    public void savePerson(Person person) {
        // Logic to save the person to the dataset
        // Logic to save the person to the dataset
        try {
            throw new UnsupportedOperationException("Method not implemented yet");
        } catch (UnsupportedOperationException e) {
            log.error("Method savePerson not implemented", e);
            throw e;
        }
    }

    public Person findByUri(String uri) {
        // Logic to find a person by ID in the dataset

        String sparql = "SELECT ?s WHERE { ?s <http://example.org/redweed#id> \"" + uri + "\" }";

        try (QueryExecution qexec = QueryExecutionFactory.create(sparql, dataset)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Resource resource = soln.getResource("s");
                return new Person(resource.getURI());
            }
        } catch (Exception e) {
            // Handle exceptions
            throw e;
        }
    }
}
