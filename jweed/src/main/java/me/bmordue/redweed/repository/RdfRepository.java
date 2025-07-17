
package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RdfRepository {
    private static final Logger log = LoggerFactory.getLogger(RdfRepository.class);

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

    public Model getDefaultModel() {
        return dataset.getDefaultModel();
    }
}
