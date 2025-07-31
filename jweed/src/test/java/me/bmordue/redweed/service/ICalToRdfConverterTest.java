package me.bmordue.redweed.service;

import me.bmordue.redweed.vocabulary.ICalVocabulary;
import net.fortuna.ical4j.data.ParserException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ICalToRdfConverterTest {

    @Test
    void testConvert() throws IOException, ParserException {
        InputStream inputStream = ICalToRdfConverterTest.class.getResourceAsStream("/me/bmordue/redweed/service/sample.ics");
        String ics = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Model model = ICalToRdfConverter.convert(ics);

        Resource vevent = model.listResourcesWithProperty(RDF.type, ICalVocabulary.VEVENT).next();
        assertEquals("Bastille Day Party", vevent.getProperty(ICalVocabulary.SUMMARY).getString());
        assertEquals("19970714T170000Z", vevent.getProperty(ICalVocabulary.DTSTART).getString());
        assertEquals("19970715T040000Z", vevent.getProperty(ICalVocabulary.DTEND).getString());
        assertEquals("Paris", vevent.getProperty(ICalVocabulary.LOCATION).getString());

        Resource vtodo = model.listResourcesWithProperty(RDF.type, ICalVocabulary.VTODO).next();
        assertEquals("Submit TPS report", vtodo.getProperty(ICalVocabulary.SUMMARY).getString());
        assertEquals("20070501T090000", vtodo.getProperty(ICalVocabulary.DTSTART).getString());

        Resource vjournal = model.listResourcesWithProperty(RDF.type, ICalVocabulary.VJOURNAL).next();
        assertEquals("Brainstorming session", vjournal.getProperty(ICalVocabulary.SUMMARY).getString());
        assertEquals("This was a very productive meeting.", vjournal.getProperty(ICalVocabulary.DESCRIPTION).getString());
    }
}
