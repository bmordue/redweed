package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.annotation.WithTestDataset;
import me.bmordue.redweed.service.EventService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WithTestDataset
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

        HttpRequest<String> request = HttpRequest.POST("/events", event)
                .contentType(io.micronaut.http.MediaType.TEXT_PLAIN);
        HttpResponse<String> response;
        response = client.toBlocking().exchange(request, String.class);
        assertEquals(io.micronaut.http.HttpStatus.OK, response.getStatus());

        verify(eventService, times(1)).ingestEvent(event);
    }

    @MockBean(EventService.class)
    private EventService eventService() {
        return mock(EventService.class);
    }
}
