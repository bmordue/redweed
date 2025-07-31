package me.bmordue.redweed.service;

import me.bmordue.redweed.vocabulary.ICalVocabulary;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.IOException;
import java.io.StringReader;

import static org.apache.jena.vocabulary.RDF.Property;

/**
 * Converts iCal data to RDF.
 */
public final class ICalToRdfConverter {

    /**
     * Converts iCal data to an RDF model.
     *
     * @param ics the iCal data
     * @return the RDF model
     * @throws IOException     if the iCal data cannot be read
     * @throws ParserException if the iCal data cannot be parsed
     */
    public static Model convert(String ics) throws IOException, ParserException {
        Model model = ModelFactory.createDefaultModel();
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new StringReader(ics));

        for (Component component : calendar.getComponents()) {
            final Resource resource;
            net.fortuna.ical4j.model.Property uidProp = component.getProperty(Property.UID);
            if (uidProp != null && uidProp.getValue() != null) {
                resource = model.createResource("urn:ical:" + uidProp.getValue());
            } else {
                resource = model.createResource();
            }
            switch (component.getName()) {
                case Component.VEVENT:
                    resource.addProperty(RDF.type, ICalVocabulary.VEVENT);
                    break;
                case Component.VTODO:
                    resource.addProperty(RDF.type, ICalVocabulary.VTODO);
                    break;
                case Component.VJOURNAL:
                    resource.addProperty(RDF.type, ICalVocabulary.VJOURNAL);
                    break;
            }

            for (Property property : component.getProperties()) {
                switch (property.getName()) {
                    case Property.SUMMARY:
                        resource.addProperty(ICalVocabulary.SUMMARY, property.getValue());
                        break;
                    case Property.DTSTART:
                        resource.addProperty(ICalVocabulary.DTSTART, property.getValue());
                        break;
                    case Property.DTEND:
                        resource.addProperty(ICalVocabulary.DTEND, property.getValue());
                        break;
                    case Property.DESCRIPTION:
                        resource.addProperty(ICalVocabulary.DESCRIPTION, property.getValue());
                        break;
                    case Property.LOCATION:
                        resource.addProperty(ICalVocabulary.LOCATION, property.getValue());
                        break;
                }
            }
        }

        return model;
    }
}
