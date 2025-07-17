package me.bmordue.redweed.vocabulary;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.config.RdfConfiguration;

/**
 * Vocabulary constants for RDF properties and resources used in the Redweed application.
 * This class centralizes URI definitions to improve maintainability and avoid typos.
 * The base namespace is now configurable via application.yml.
 */
@Singleton
public class RedweedVocab {
    
    private final String baseNamespace;
    private final String personNamespace;
    private final String placeNamespace;
    private final String reviewNamespace;
    
    @Inject
    public RedweedVocab(RdfConfiguration.NamespaceConfiguration namespaceConfig) {
        this.baseNamespace = namespaceConfig.getBase();
        this.personNamespace = baseNamespace + "person/";
        this.placeNamespace = baseNamespace + "place/";
        this.reviewNamespace = baseNamespace + "review/";
    }
    
    // Base namespace for local resources
    public String getBaseNamespace() {
        return baseNamespace;
    }
    
    // Resource type namespaces
    public String getPersonNamespace() {
        return personNamespace;
    }
    
    public String getPlaceNamespace() {
        return placeNamespace;
    }
    
    public String getReviewNamespace() {
        return reviewNamespace;
    }
    
    // W3C Geo vocabulary (these remain static as they are external standards)
    public static final String GEO_NAMESPACE = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String GEO_SPATIAL_THING = GEO_NAMESPACE + "SpatialThing";
    public static final String GEO_LAT = GEO_NAMESPACE + "lat";
    public static final String GEO_LONG = GEO_NAMESPACE + "long";
    
    // Review vocabulary (these remain static as they are external standards)
    public static final String REV_NAMESPACE = "http://purl.org/stuff/rev#";
    public static final String REV_REVIEW = REV_NAMESPACE + "Review";
    public static final String REV_RATING = REV_NAMESPACE + "rating";
    public static final String REV_TEXT = REV_NAMESPACE + "text";
    public static final String REV_REVIEWER = REV_NAMESPACE + "reviewer";
}
