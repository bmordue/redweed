package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.JenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository for RDF data.
 */
@Singleton
public class RdfRepository {
    private static final Logger log = LoggerFactory.getLogger(RdfRepository.class);

    protected final Dataset dataset;

    /**
     * Constructor.
     *
     * @param dataset the dataset
     */
    @Inject
    public RdfRepository(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Save a model to the dataset.
     *
     * @param model the model to save
     */
    public void save(Model model) {
        dataset.begin(ReadWrite.WRITE);
        try {
            dataset.getDefaultModel().add(model);
            dataset.commit();
        } catch (JenaException e) {
            dataset.abort();
            log.error("Error saving model to dataset", e);
            throw e;
        } finally {
            dataset.end();
        }
    }
}
