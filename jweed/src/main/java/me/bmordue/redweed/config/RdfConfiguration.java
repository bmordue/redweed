package me.bmordue.redweed.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb2.TDB2Factory;

@Factory
public class RdfConfiguration {

    @ConfigurationProperties("rdf.dataset")
    public static class DatasetConfiguration {
        private String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    @ConfigurationProperties("rdf.namespaces")
    public static class NamespaceConfiguration {
        private String base = "http://redweed.local/";

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }
    }

    @Singleton
    public Dataset dataset(DatasetConfiguration config) {
        return TDB2Factory.connectDataset(config.getLocation());
    }

}
