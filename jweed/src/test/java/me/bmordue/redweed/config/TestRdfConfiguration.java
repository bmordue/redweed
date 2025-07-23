package me.bmordue.redweed.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb2.TDB2Factory;

@Factory
@Replaces(factory = RdfConfiguration.class)
public class TestRdfConfiguration {
    @Singleton
    public Dataset dataset() {
        // This creates a non-persistent, in-memory dataset.
        return TDB2Factory.createDataset();
    }
}