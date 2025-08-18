package me.bmordue.redweed.service;

import me.bmordue.redweed.exception.ICalParsingException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ICalToRdfConverterTest {

    @Test
    void testConvertBasicEvent() {
        // Given - using the actual basic-event.ics content
        String ical = """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Example Corp//Example Calendar//EN
                CALSCALE:GREGORIAN
                METHOD:PUBLISH
                BEGIN:VEVENT
                UID:basic-event-001@example.com
                DTSTART:20240315T100000Z
                DTEND:20240315T110000Z
                SUMMARY:Project Planning Meeting
                DESCRIPTION:Weekly project planning session to review progress and set priorities for the upcoming week.
                LOCATION:Conference Room A, Building 1
                ORGANIZER;CN=Alice Smith:MAILTO:alice@example.com
                ATTENDEE;CN=Bob Johnson:MAILTO:bob@example.com
                ATTENDEE;CN=Carol Davis:MAILTO:carol@example.com
                CATEGORIES:work,meetings,planning
                PRIORITY:5
                STATUS:CONFIRMED
                END:VEVENT
                END:VCALENDAR
                """;

        // When
        Model model = ICalToRdfConverter.convert(ical);

        // Then
        assertNotNull(model);
        assertFalse(model.isEmpty());
        
        // Should have multiple statements (properties)
        assertTrue(model.size() > 5);
        
        // Check for expected properties
        List<Statement> statements = model.listStatements().toList();
        boolean hasLabel = statements.stream().anyMatch(s -> 
            s.getPredicate().equals(RDFS.label) && 
            s.getObject().toString().contains("Project Planning Meeting"));
        assertTrue(hasLabel, "Should have label/summary");
        
        boolean hasDescription = statements.stream().anyMatch(s -> 
            s.getPredicate().equals(DC.description) && 
            s.getObject().toString().contains("project planning"));
        assertTrue(hasDescription, "Should have description");
    }

    @Test
    void testConvertWithNullInput() {
        assertThrows(ICalParsingException.class, () -> {
            ICalToRdfConverter.convert(null);
        });
    }

    @Test
    void testConvertWithEmptyInput() {
        assertThrows(ICalParsingException.class, () -> {
            ICalToRdfConverter.convert("");
        });
    }

    @Test
    void testConvertWithInvalidICalData() {
        String invalidIcal = "This is not valid iCal data";
        
        assertThrows(ICalParsingException.class, () -> {
            ICalToRdfConverter.convert(invalidIcal);
        });
    }

    @Test
    void testConvertMultipleEvents() {
        // Given
        String ical = """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Example Corp//Example Calendar//EN
                BEGIN:VEVENT
                UID:event1@example.com
                DTSTART:20240315T100000Z
                DTEND:20240315T110000Z
                SUMMARY:First Event
                END:VEVENT
                BEGIN:VEVENT
                UID:event2@example.com
                DTSTART:20240316T140000Z
                DTEND:20240316T150000Z
                SUMMARY:Second Event
                END:VEVENT
                END:VCALENDAR
                """;

        // When
        Model model = ICalToRdfConverter.convert(ical);

        // Then
        assertNotNull(model);
        assertFalse(model.isEmpty());
        
        // Should have statements for both events
        List<Statement> statements = model.listStatements().toList();
        boolean hasFirstEvent = statements.stream().anyMatch(s -> 
            s.getObject().toString().contains("First Event"));
        boolean hasSecondEvent = statements.stream().anyMatch(s -> 
            s.getObject().toString().contains("Second Event"));
            
        assertTrue(hasFirstEvent, "Should contain first event");
        assertTrue(hasSecondEvent, "Should contain second event");
    }
}