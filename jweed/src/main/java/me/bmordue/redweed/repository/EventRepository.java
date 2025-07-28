package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;

/**
 * Repository for events.
 */
public class EventRepository extends RdfRepository {
    /**
     * Constructor.
     *
     * @param dataset the dataset
     */
    @Inject
    public EventRepository(Dataset dataset) {
        super(dataset);
    }
}
