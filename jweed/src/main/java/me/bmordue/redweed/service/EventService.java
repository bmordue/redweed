package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import me.bmordue.redweed.exception.ICalParsingException;
import me.bmordue.redweed.model.dto.IngestICalResponseDto;
import me.bmordue.redweed.repository.EventRepository;
import org.apache.jena.rdf.model.Model;

/**
 * Service for events.
 */
public class EventService {
    private final EventRepository eventRepository;

    /**
     * Constructor.
     *
     * @param eventRepository the event repository
     */
    @Inject
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Ingest an iCal file.
     *
     * @param ics the iCal file content
     * @return the response
     */
    public IngestICalResponseDto ingestEvent(String ics) {
        Model model;
        try {
            model = ICalToRdfConverter.convert(ics);
        } catch (ICalParsingException e) { // Replace RuntimeException with the specific exception
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Could not import event");
        }

        eventRepository.save(model);

        return new IngestICalResponseDto();
    }
}
