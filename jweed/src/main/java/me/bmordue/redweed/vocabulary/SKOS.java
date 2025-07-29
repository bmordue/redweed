package me.bmordue.redweed.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * SKOS vocabulary.
 *
 * @see https://www.w3.org/TR/skos-reference/
 */
public final class SKOS {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.w3.org/2004/02/skos/core#";
    /**
     * The namespace of the vocabulary as a resource.
     */
    public static final Resource NAMESPACE = ResourceFactory.createResource(NS);
    /**
     * Properties
     */
    public static final Property MEMBER = ResourceFactory.createProperty(NS + "member");
    /**
     * Classes
     */
    public static final Resource COLLECTION = ResourceFactory.createResource(NS + "Collection");
    public static final Resource ORDERED_COLLECTION = ResourceFactory.createResource(NS + "OrderedCollection");

    private SKOS() {
        // Private constructor to prevent instantiation
    }
}
