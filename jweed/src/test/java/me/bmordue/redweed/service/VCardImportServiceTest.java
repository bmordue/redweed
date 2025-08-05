package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import me.bmordue.redweed.controller.VCardImportRequest;
import me.bmordue.redweed.model.Addressbook;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for VCardImportService.
 */
@ExtendWith(MockitoExtension.class)
class VCardImportServiceTest {

    @Mock
    private CaldavService caldavService;

    @Mock
    private PersonRepository personRepository;

    private VCardImportService vCardImportService;

    @BeforeEach
    void setUp() {
        vCardImportService = new VCardImportService(caldavService, personRepository);
    }

    @Test
    void testImportVCardsSuccess() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/"),
                new Addressbook("Work", "https://caldav.example.com/work/")
        );

        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:John Doe
                EMAIL:john@example.com
                END:VCARD
                """,
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Jane Smith
                EMAIL:jane@example.com
                END:VCARD
                """
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);
        when(caldavService.fetchVCards(any(Addressbook.class), anyString(), anyString()))
                .thenReturn(vcards);

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCards(request);

        // Assert
        assertNotNull(response);
        assertEquals("Import completed: 2 successful, 0 errors", response.getMessage());

        verify(caldavService).discoverAddressbooks(request.getCaldavUrl(), request.getUsername(), request.getPassword());
        verify(caldavService).fetchVCards(any(Addressbook.class), eq(request.getUsername()), eq(request.getPassword()));
        verify(personRepository, times(2)).save(any(Model.class));
    }

    @Test
    void testImportVCardsAddressbookNotFound() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "NonExistent"
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/"),
                new Addressbook("Work", "https://caldav.example.com/work/")
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);

        // Act & Assert
        HttpStatusException exception = assertThrows(HttpStatusException.class, () ->
                vCardImportService.importVCards(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Addressbook not found: NonExistent"));

        verify(caldavService).discoverAddressbooks(request.getCaldavUrl(), request.getUsername(), request.getPassword());
        verify(caldavService, never()).fetchVCards(any(), anyString(), anyString());
        verify(personRepository, never()).save(any(Model.class));
    }

    @Test
    void testImportVCardsNoVCardsFound() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/")
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);
        when(caldavService.fetchVCards(any(Addressbook.class), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCards(request);

        // Assert
        assertNotNull(response);
        assertEquals("No vCards found in addressbook", response.getMessage());

        verify(caldavService).discoverAddressbooks(request.getCaldavUrl(), request.getUsername(), request.getPassword());
        verify(caldavService).fetchVCards(any(Addressbook.class), eq(request.getUsername()), eq(request.getPassword()));
        verify(personRepository, never()).save(any(Model.class));
    }

    @Test
    void testImportVCardsWithSomeErrors() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/")
        );

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

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);
        when(caldavService.fetchVCards(any(Addressbook.class), anyString(), anyString()))
                .thenReturn(vcards);
        
        // Mock repository to succeed for first call, but the invalid vCard will cause VCardToRdfConverter to fail
        doNothing().when(personRepository).save(any(Model.class));

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCards(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("successful"));
        assertTrue(response.getMessage().contains("errors"));

        verify(caldavService).discoverAddressbooks(request.getCaldavUrl(), request.getUsername(), request.getPassword());
        verify(caldavService).fetchVCards(any(Addressbook.class), eq(request.getUsername()), eq(request.getPassword()));
    }

    @Test
    void testImportVCardsServiceError() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Personal"
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("CalDAV server error"));

        // Act & Assert
        HttpStatusException exception = assertThrows(HttpStatusException.class, () ->
                vCardImportService.importVCards(request));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertTrue(exception.getMessage().contains("Failed to import vCards"));

        verify(caldavService).discoverAddressbooks(request.getCaldavUrl(), request.getUsername(), request.getPassword());
        verify(personRepository, never()).save(any(Model.class));
    }

    @Test
    void testImportVCardBatchSuccess() {
        // Arrange
        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:John Doe
                EMAIL:john@example.com
                END:VCARD
                """,
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Jane Smith
                EMAIL:jane@example.com
                END:VCARD
                """
        );

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCardBatch(vcards);

        // Assert
        assertNotNull(response);
        assertEquals("Batch import completed: 2 successful, 0 errors", response.getMessage());

        verify(personRepository, times(2)).save(any(Model.class));
    }

    @Test
    void testImportVCardBatchEmpty() {
        // Arrange
        List<String> vcards = Collections.emptyList();

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCardBatch(vcards);

        // Assert
        assertNotNull(response);
        assertEquals("No vCards provided", response.getMessage());

        verify(personRepository, never()).save(any(Model.class));
    }

    @Test
    void testImportVCardBatchWithErrors() {
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
        IngestVCardResponseDto response = vCardImportService.importVCardBatch(vcards);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("successful"));
        assertTrue(response.getMessage().contains("errors"));
    }

    @Test
    void testListAddressbooksSuccess() {
        // Arrange
        String caldavUrl = "https://caldav.example.com";
        String username = "testuser";
        String password = "testpass";

        List<Addressbook> expectedAddressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/"),
                new Addressbook("Work", "https://caldav.example.com/work/")
        );

        when(caldavService.discoverAddressbooks(caldavUrl, username, password))
                .thenReturn(expectedAddressbooks);

        // Act
        List<Addressbook> addressbooks = vCardImportService.listAddressbooks(caldavUrl, username, password);

        // Assert
        assertNotNull(addressbooks);
        assertEquals(2, addressbooks.size());
        assertEquals(expectedAddressbooks, addressbooks);

        verify(caldavService).discoverAddressbooks(caldavUrl, username, password);
    }

    @Test
    void testListAddressbooksError() {
        // Arrange
        String caldavUrl = "https://caldav.example.com";
        String username = "testuser";
        String password = "testpass";

        when(caldavService.discoverAddressbooks(caldavUrl, username, password))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        HttpStatusException exception = assertThrows(HttpStatusException.class, () ->
                vCardImportService.listAddressbooks(caldavUrl, username, password));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertTrue(exception.getMessage().contains("Failed to list addressbooks"));

        verify(caldavService).discoverAddressbooks(caldavUrl, username, password);
    }

    @Test
    void testFindAddressbookByName() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Work"  // Search by name
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/", "Personal contacts", "Personal Addressbook"),
                new Addressbook("Work", "https://caldav.example.com/work/", "Work contacts", "Work Addressbook")
        );

        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Bob Johnson
                EMAIL:bob@company.com
                END:VCARD
                """
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);
        when(caldavService.fetchVCards(any(Addressbook.class), anyString(), anyString()))
                .thenReturn(vcards);

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCards(request);

        // Assert
        assertNotNull(response);
        assertEquals("Import completed: 1 successful, 0 errors", response.getMessage());
    }

    @Test
    void testFindAddressbookByDisplayName() {
        // Arrange
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com",
                "testuser",
                "testpass",
                "Work Addressbook"  // Search by display name
        );

        List<Addressbook> addressbooks = Arrays.asList(
                new Addressbook("Personal", "https://caldav.example.com/personal/", "Personal contacts", "Personal Addressbook"),
                new Addressbook("Work", "https://caldav.example.com/work/", "Work contacts", "Work Addressbook")
        );

        List<String> vcards = Arrays.asList(
                """
                BEGIN:VCARD
                VERSION:4.0
                FN:Bob Johnson
                EMAIL:bob@company.com
                END:VCARD
                """
        );

        when(caldavService.discoverAddressbooks(anyString(), anyString(), anyString()))
                .thenReturn(addressbooks);
        when(caldavService.fetchVCards(any(Addressbook.class), anyString(), anyString()))
                .thenReturn(vcards);

        // Act
        IngestVCardResponseDto response = vCardImportService.importVCards(request);

        // Assert
        assertNotNull(response);
        assertEquals("Import completed: 1 successful, 0 errors", response.getMessage());
    }
}