package me.bmordue.redweed.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary for iCalendar data.
 */
public final class ICalVocabulary {

    /**
     * The namespace of the vocabulary.
     */
    public static final String NS = "http://www.w3.org/2002/12/cal/ical#";

    /**
     * The VEVENT class.
     */
    public static final Resource VEVENT = ResourceFactory.createResource(NS + "Vevent");

    /**
     * The VTODO class.
     */
    public static final Resource VTODO = ResourceFactory.createResource(NS + "Vtodo");

    /**
     * The VJOURNAL class.
     */
    public static final Resource VJOURNAL = ResourceFactory.createResource(NS + "Vjournal");

    /**
     * The summary property.
     */
    public static final Property SUMMARY = ResourceFactory.createProperty(NS + "summary");

    /**
     * The dtstart property.
     */
    public static final Property DTSTART = ResourceFactory.createProperty(NS + "dtstart");

    /**
     * The dtend property.
     */
    public static final Property DTEND = ResourceFactory.createProperty(NS + "dtend");

    /**
     * The description property.
     */
    public static final Property DESCRIPTION = ResourceFactory.createProperty(NS + "description");

    /**
     * The location property.
     */
    public static final Property LOCATION = ResourceFactory.createProperty(NS + "location");

    /**
     * Private constructor to prevent instantiation.
     */
    private ICalVocabulary() {
    }
}
