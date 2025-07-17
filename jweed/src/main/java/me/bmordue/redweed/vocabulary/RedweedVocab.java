package me.bmordue.redweed.vocabulary;

/**
 * Vocabulary constants for RDF properties and resources used in the Redweed application.
 * This class centralizes URI definitions to improve maintainability and avoid typos.
 */
public final class RedweedVocab {
    
    // Private constructor to prevent instantiation
    private RedweedVocab() {}
    
    // Base namespace for local resources
    public static final String REDWEED_NAMESPACE = "http://redweed.local/";
    
    // Resource type namespaces
    public static final String PERSON_NAMESPACE = REDWEED_NAMESPACE + "person/";
    public static final String PLACE_NAMESPACE = REDWEED_NAMESPACE + "place/";
    public static final String REVIEW_NAMESPACE = REDWEED_NAMESPACE + "review/";
    
    // W3C Geo vocabulary
    public static final String GEO_NAMESPACE = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String GEO_SPATIAL_THING = GEO_NAMESPACE + "SpatialThing";
    public static final String GEO_LAT = GEO_NAMESPACE + "lat";
    public static final String GEO_LONG = GEO_NAMESPACE + "long";
    
    // Review vocabulary
    public static final String REV_NAMESPACE = "http://purl.org/stuff/rev#";
    public static final String REV_REVIEW = REV_NAMESPACE + "Review";
    public static final String REV_RATING = REV_NAMESPACE + "rating";
    public static final String REV_TEXT = REV_NAMESPACE + "text";
    public static final String REV_REVIEWER = REV_NAMESPACE + "reviewer";
}
