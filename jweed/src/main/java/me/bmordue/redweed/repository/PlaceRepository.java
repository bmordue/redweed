package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PlaceRepository extends RdfRepository {

    private static final Logger log = LoggerFactory.getLogger(PlaceRepository.class);

    @Inject
    private Dataset dataset;

    public void save(Model model) {
        super.save(model);
    }
}
