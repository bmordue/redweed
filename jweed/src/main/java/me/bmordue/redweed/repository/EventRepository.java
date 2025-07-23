package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import org.apache.jena.query.Dataset;

public class EventRepository extends RdfRepository {
    @Inject
    public EventRepository(Dataset dataset) {
        super(dataset);
    }
}
