package me.bmordue.redweed.controller;

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

    @Post
    public void ingestEvent(String body) {
        eventService.ingestEvent(body);
    }

}
