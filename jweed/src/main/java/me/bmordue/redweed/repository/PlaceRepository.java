package me.bmordue.redweed.repository;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;

/**
 * Repository for places.
 */
@Singleton
public class PlaceRepository extends RdfRepository {

    private static final Logger log = LoggerFactory.getLogger(PlaceRepository.class);

    /**
     * Constructor.
     *
     * @param dataset the dataset
     */
    @Inject
    public PlaceRepository(Dataset dataset) {
        super(dataset);
    }
}
