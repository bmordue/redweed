package me.bmordue.redweed.service;

import me.bmordue.redweed.repository.EventRepository;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
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
        verify(eventRepository).save(any(Model.class));
    }
}
