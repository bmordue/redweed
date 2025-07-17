package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestICalResponseDto;
import me.bmordue.redweed.repository.EventRepository;
import org.apache.jena.rdf.model.Model;

public class EventService {
    private final EventRepository eventRepository;

    @Inject
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public IngestICalResponseDto ingestEvent(String ics) {
        Model model;
        try {
            model = ICalToRdfConverter.convert(ics);
        } catch (RuntimeException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Could not import event");
        }

        eventRepository.save(model);

        return new IngestICalResponseDto();
    }
}
