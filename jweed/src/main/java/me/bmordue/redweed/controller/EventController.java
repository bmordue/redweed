package me.bmordue.redweed.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.service.EventService;

@Controller("/events")
public class EventController {

    private final EventService eventService;

    @Inject
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Post(consumes = MediaType.TEXT_PLAIN)
    public io.micronaut.http.HttpResponse<String> ingestEvent(@Body String body) {
        eventService.ingestEvent(body);
        return io.micronaut.http.HttpResponse.ok();
    }

}
