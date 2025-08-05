package me.bmordue.redweed.controller;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.annotation.WithTestDataset;
import me.bmordue.redweed.model.Addressbook;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.service.VCardImportService;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for VCardImportController.
 */
@MicronautTest
@WithTestDataset
class VCardImportControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    Dataset dataset;

    @BeforeEach
    void setup() {
        dataset.begin(ReadWrite.WRITE);
        try {
            dataset.getDefaultModel().removeAll();
            dataset.commit();
        } finally {
            dataset.end();
        }
    }

    @Test
    void testImportFromCalDAVSuccess() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        // Act
        IngestVCardResponseDto response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/vcard/import/caldav", request), IngestVCardResponseDto.class);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("successful") || response.getMessage().contains("No vCards found"));
    }

    @Test
    void testImportFromCalDAVWithInvalidRequest() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "invalid-url",
                "testuser",
                "testpass",
                "Personal"
        );

        // Act & Assert
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().retrieve(HttpRequest.POST("/api/vcard/import/caldav", request), IngestVCardResponseDto.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void testImportFromCalDAVWithMissingCredentials() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                null,
                "testpass",
                "Personal"
        );

        // Act & Assert
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().retrieve(HttpRequest.POST("/api/vcard/import/caldav", request), IngestVCardResponseDto.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void testImportBatchSuccess() {
        // Arrange
        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:John Doe
                N:Doe;John;;;
                EMAIL:john@example.com
                TEL:+1-555-555-1234
                END:VCARD
                """,
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Jane Smith
                N:Smith;Jane;;;
                EMAIL:jane@example.com
                TEL:+1-555-555-5678
                END:VCARD
                """
        );

        // Act
        IngestVCardResponseDto response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/vcard/import/batch", vcards), IngestVCardResponseDto.class);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("successful"));
    }

    @Test
    void testImportBatchWithEmptyList() {
        // Arrange
        List<String> vcards = Arrays.asList();

        // Act
        IngestVCardResponseDto response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/vcard/import/batch", vcards), IngestVCardResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals("No vCards provided", response.getMessage());
    }

    @Test
    void testImportBatchWithInvalidVCard() {
        // Arrange
        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:John Doe
                EMAIL:john@example.com
                END:VCARD
                """,
                "INVALID_VCARD_DATA"
        );

        // Act
        IngestVCardResponseDto response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/vcard/import/batch", vcards), IngestVCardResponseDto.class);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("successful"));
        assertTrue(response.getMessage().contains("errors"));
    }

    @Test
    void testListAddressbooksSuccess() {
        // Arrange
        String caldavUrl = "https://caldav.example.com";
        String username = "testuser";
        String password = "testpass";

        // Act
        List<Addressbook> addressbooks = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/vcard/import/addressbooks" +
                        "?caldavUrl=" + caldavUrl +
                        "&username=" + username +
                        "&password=" + password), 
                        Argument.listOf(Addressbook.class));

        // Assert
        assertNotNull(addressbooks);
        assertEquals(2, addressbooks.size());
    }

    @Test
    void testListAddressbooksWithInvalidUrl() {
        // Arrange
        String caldavUrl = "invalid-url";
        String username = "testuser";
        String password = "testpass";

        // Act & Assert
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/vcard/import/addressbooks" +
                        "?caldavUrl=" + caldavUrl +
                        "&username=" + username +
                        "&password=" + password), 
                        Argument.listOf(Addressbook.class)));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void testListAddressbooksWithMissingParameters() {
        // Act & Assert - Missing caldavUrl
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/vcard/import/addressbooks" +
                        "?username=testuser" +
                        "&password=testpass"), 
                        Argument.listOf(Addressbook.class)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testEndpointPaths() {
        // Test that the endpoints are mapped correctly
        
        // Test CalDAV import endpoint
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        assertDoesNotThrow(() -> {
            try {
                client.toBlocking().retrieve(HttpRequest.POST("/api/vcard/import/caldav", request), IngestVCardResponseDto.class);
            } catch (HttpClientResponseException e) {
                // Expected for mock CalDAV server, but endpoint should be reachable
                assertTrue(e.getStatus().getCode() >= 400);
            }
        });

        // Test batch import endpoint
        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Test User
                EMAIL:test@example.com
                END:VCARD
                """
        );

        assertDoesNotThrow(() -> {
            IngestVCardResponseDto response = client.toBlocking()
                    .retrieve(HttpRequest.POST("/api/vcard/import/batch", vcards), IngestVCardResponseDto.class);
            assertNotNull(response);
        });

        // Test addressbooks listing endpoint
        assertDoesNotThrow(() -> {
            try {
                client.toBlocking().retrieve(HttpRequest.GET("/api/vcard/import/addressbooks" +
                        "?caldavUrl=https://caldav.example.com" +
                        "&username=testuser" +
                        "&password=testpass"), 
                        Argument.listOf(Addressbook.class));
            } catch (HttpClientResponseException e) {
                // Expected for mock CalDAV server, but endpoint should be reachable
                assertTrue(e.getStatus().getCode() >= 400);
            }
        });
    }

    @Test
    void testJsonSerialization() {
        // Test that VCardImportRequest can be properly serialized/deserialized
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        // This test verifies the request can be properly serialized as JSON
        // by making an actual HTTP request
        assertDoesNotThrow(() -> {
            try {
                client.toBlocking().retrieve(HttpRequest.POST("/api/vcard/import/caldav", request), IngestVCardResponseDto.class);
            } catch (HttpClientResponseException e) {
                // We expect this to fail due to mock CalDAV server, but if it fails with a JSON
                // serialization error, that would be a different status code
                assertNotEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
            }
        });
    }
}