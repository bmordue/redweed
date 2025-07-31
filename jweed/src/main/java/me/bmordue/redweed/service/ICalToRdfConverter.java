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
import java.util.Optional;

/**
 * Converts iCal data to RDF.
 */
public final class ICalToRdfConverter {

    /**
     * Converts iCal data to an RDF model.
     *
     * @param ics the iCal data
     * @return the RDF model
     * @throws IOException if the iCal data cannot be read
     * @throws ParserException if the iCal data cannot be parsed
     */
    public static Model convert(String ics) throws IOException, ParserException {
        Model model = ModelFactory.createDefaultModel();
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new StringReader(ics));

        for (Component component : calendar.getComponents()) {
            Resource resource = model.createResource();
            if (component.getName().equals(Component.VEVENT)) {
                resource.addProperty(RDF.type, ICalVocabulary.VEVENT);
            } else if (component.getName().equals(Component.VTODO)) {
                resource.addProperty(RDF.type, ICalVocabulary.VTODO);
            } else if (component.getName().equals(Component.VJOURNAL)) {
                resource.addProperty(RDF.type, ICalVocabulary.VJOURNAL);
            }

            for (Property property : component.getProperties()) {
                if (property.getName().equals(Property.SUMMARY)) {
                    resource.addProperty(ICalVocabulary.SUMMARY, property.getValue());
                } else if (property.getName().equals(Property.DTSTART)) {
                    resource.addProperty(ICalVocabulary.DTSTART, property.getValue());
                } else if (property.getName().equals(Property.DTEND)) {
                    resource.addProperty(ICalVocabulary.DTEND, property.getValue());
                } else if (property.getName().equals(Property.DESCRIPTION)) {
                    resource.addProperty(ICalVocabulary.DESCRIPTION, property.getValue());
                } else if (property.getName().equals(Property.LOCATION)) {
                    resource.addProperty(ICalVocabulary.LOCATION, property.getValue());
                }
            }
        }

        return model;
    }
}
