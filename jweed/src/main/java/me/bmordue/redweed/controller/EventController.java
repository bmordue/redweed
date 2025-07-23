package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.service.EventService;

@Controller("/events")
public class EventController {

    @Inject
    private EventService eventService;

    @Post
    public io.micronaut.http.HttpResponse<String> ingestEvent(String body) {
        eventService.ingestEvent(body);
        return io.micronaut.http.HttpResponse.ok();
    }

}
