package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.annotation.WithTestDataset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithTestDataset
@MicronautTest
public class PersonControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testIngestInvalidVCard() {
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().retrieve(HttpRequest.POST("/persons", "this is not a vcard"));
        });

        assertEquals(400, exception.getStatus().getCode());
    }
}
