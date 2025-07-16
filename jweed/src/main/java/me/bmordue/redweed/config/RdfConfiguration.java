package me.bmordue.redweed.config;

import javax.swing.SwingContainer;
import jakarta.inject.Singleton;

@Factory
public class RdfConfiguration {

    @ConfigurationProperties("rdf.dataset")
    public static class DatasetConfiguration {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Singleton
    public Dataset dataset(DatasetConfiguration config) {
        return TDB2Factory.createDataset(config.getName(), config.getType());
    }

}
