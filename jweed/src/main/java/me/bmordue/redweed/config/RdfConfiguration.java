package me.bmordue.redweed.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb2.TDB2Factory;

/**
 * Configuration for RDF.
 */
@Factory
public class RdfConfiguration {

    /**
     * Configuration for the RDF dataset.
     */
    @ConfigurationProperties("rdf.dataset")
    public static class DatasetConfiguration {
        private String location;

        /**
         * Get the location of the dataset.
         * @return the location of the dataset
         */
        public String getLocation() {
            return location;
        }

        /**
         * Set the location of the dataset.
         * @param location the location of the dataset
         */
        public void setLocation(String location) {
            this.location = location;
        }
    }

    /**
     * Configuration for the RDF namespaces.
     */
    @ConfigurationProperties("rdf.namespaces")
    public static class NamespaceConfiguration {
        private String base = "http://redweed.local/";

        /**
         * Get the base namespace.
         * @return the base namespace
         */
        public String getBase() {
            return base;
        }

        /**
         * Set the base namespace.
         * @param base the base namespace
         */
        public void setBase(String base) {
            this.base = base;
        }
    }

    /**
     * Create a dataset.
     * @param config the dataset configuration
     * @return the dataset
     */
    @Singleton
    public Dataset dataset(DatasetConfiguration config) {
        return TDB2Factory.connectDataset(config.getLocation());
    }

}
