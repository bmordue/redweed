package me.bmordue.redweed.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.service.EventService;

/**
 * Controller for handling events.
 */
@Controller("/events")
public class EventController {

    private final EventService eventService;

    /**
     * Constructor.
     *
     * @param eventService the event service
     */
    @Inject
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Ingest an event.
     *
     * @param body the event body
     * @return the response
     */
    @Post(consumes = MediaType.TEXT_PLAIN)
    public io.micronaut.http.HttpResponse<String> ingestEvent(@Body String body) {
        eventService.ingestEvent(body);
        return io.micronaut.http.HttpResponse.ok();
    }

}
