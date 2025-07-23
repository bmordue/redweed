package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.EventRepository;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void ingestEvent() {
        // Given
        String ical = """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//hacksw/handcal//NONSGML v1.0//EN
                BEGIN:VEVENT
                UID:uid1@example.com
                DTSTAMP:19970714T170000Z
                ORGANIZER;CN=John Doe:MAILTO:john.doe@example.com
                DTSTART:19970714T170000Z
                DTEND:19970715T035959Z
                SUMMARY:Bastille Day Party
                END:VEVENT
                END:VCALENDAR
                """;

        // When
        eventService.ingestEvent(ical);

        // Then
        ArgumentCaptor<Model> modelCaptor = ArgumentCaptor.forClass(Model.class);
        verify(eventRepository).save(modelCaptor.capture());

        Model capturedModel = modelCaptor.getValue();
        assertNotNull(capturedModel, "Model should not be null");

        // For now, since ICalToRdfConverter returns an empty model, we verify it's empty
        // TODO: Once ICalToRdfConverter is implemented, add assertions for:
        // - Event UID should be present: uid1@example.com
        // - Event summary should be: "Bastille Day Party"
        // - Event start time should be: 1997-07-14T17:00:00Z
        // - Event end time should be: 1997-07-15T03:59:59Z
        // - Organizer should be: John Doe (john.doe@example.com)
        assertTrue(capturedModel.isEmpty(), "Model should be empty until ICalToRdfConverter is implemented");
    }
}
