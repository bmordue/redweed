package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
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
            log.error("Transaction failed, aborting.", e);
            dataset.abort();
            throw e;
        } 
    }
}
