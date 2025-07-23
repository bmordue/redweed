package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.service.EventService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@MicronautTest
class EventControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    private EventService eventService;

    @Test
    void testIngestEvent() {
        String event = "BEGIN:VCALENDAR...";

        HttpRequest<String> request = HttpRequest.POST("/events", event);
        client.toBlocking().exchange(request);

        verify(eventService, times(1)).ingestEvent(event);
    }

    @MockBean(EventService.class)
    private EventService eventService() {
        return mock(EventService.class);
    }
}
