package me.bmordue.redweed.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.CaldavImportResponseDto;
import me.bmordue.redweed.service.CaldavService;
import me.bmordue.redweed.service.PersonService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
class CaldavControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    CaldavService caldavService;

    @Inject
    PersonService personService;

    @Test
    void testImportFromCaldavSuccess() {
        // Given
        when(caldavService.fetchVCardResources()).thenReturn(List.of("vcard1", "vcard2"));
        doNothing().when(personService).ingestVCard(anyString());

        // When
        HttpRequest<Object> request = HttpRequest.POST("/caldav/import", "");
        HttpResponse<CaldavImportResponseDto> response = client.toBlocking().exchange(request, CaldavImportResponseDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Import complete", response.body().message());
        assertEquals(2, response.body().importedCount());

        verify(caldavService, times(1)).fetchVCardResources();
        verify(personService, times(2)).ingestVCard(anyString());
    }

    @Test
    void testImportFromCaldavServiceThrowsException() {
        // Given
        when(caldavService.fetchVCardResources()).thenThrow(new RuntimeException("Test Exception"));

        // When
        HttpRequest<Object> request = HttpRequest.POST("/caldav/import", "");
        HttpResponse<CaldavImportResponseDto> response = client.toBlocking().exchange(request, CaldavImportResponseDto.class);


        // Then
        // The controller catches the exception and returns a 0 count.
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, response.body().importedCount());

        verify(caldavService, times(1)).fetchVCardResources();
        verify(personService, never()).ingestVCard(anyString());
    }

    @MockBean(CaldavService.class)
    CaldavService caldavService() {
        return mock(CaldavService.class);
    }

    @MockBean(PersonService.class)
    PersonService personService() {
        return mock(PersonService.class);
    }
}
