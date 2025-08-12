package me.bmordue.redweed.service;

import io.micronaut.http.client.HttpClient;
import me.bmordue.redweed.model.Addressbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CaldavService.
 */
class CaldavServiceTest {

    @Mock
    private HttpClient httpClient;

    private CaldavService caldavService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caldavService = new CaldavService(httpClient);
    }

    @Test
    void testDiscoverAddressbooksSuccess() {
        String caldavUrl = "https://caldav.example.com";
        String username = "testuser";
        String password = "testpass";
        
        List<Addressbook> addressbooks = caldavService.discoverAddressbooks(caldavUrl, username, password);
        
        assertNotNull(addressbooks);
        assertEquals(2, addressbooks.size());
        
        Addressbook personal = addressbooks.get(0);
        assertEquals("Personal", personal.getName());
        assertEquals(caldavUrl + "/addressbooks/personal/", personal.getUrl());
        assertEquals("Personal contacts", personal.getDescription());
        assertEquals("Personal Addressbook", personal.getDisplayName());
        
        Addressbook work = addressbooks.get(1);
        assertEquals("Work", work.getName());
        assertEquals(caldavUrl + "/addressbooks/work/", work.getUrl());
        assertEquals("Work contacts", work.getDescription());
        assertEquals("Work Addressbook", work.getDisplayName());
    }

    @Test
    void testDiscoverAddressbooksWithInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("invalid-url", "user", "pass"));
    }

    @Test
    void testDiscoverAddressbooksWithNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks(null, "user", "pass"));
    }

    @Test
    void testDiscoverAddressbooksWithEmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("", "user", "pass"));
    }

    @Test
    void testDiscoverAddressbooksWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("https://caldav.example.com", null, "pass"));
    }

    @Test
    void testDiscoverAddressbooksWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("https://caldav.example.com", "", "pass"));
    }

    @Test
    void testDiscoverAddressbooksWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("https://caldav.example.com", "user", null));
    }

    @Test
    void testDiscoverAddressbooksWithEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.discoverAddressbooks("https://caldav.example.com", "user", ""));
    }

    @Test
    void testFetchVCardsFromPersonalAddressbook() {
        Addressbook addressbook = new Addressbook("Personal", "https://caldav.example.com/personal/");
        String username = "testuser";
        String password = "testpass";
        
        List<String> vcards = caldavService.fetchVCards(addressbook, username, password);
        
        assertNotNull(vcards);
        assertEquals(2, vcards.size());
        
        String firstVCard = vcards.get(0);
        assertTrue(firstVCard.contains("BEGIN:VCARD"));
        assertTrue(firstVCard.contains("FN:John Doe"));
        assertTrue(firstVCard.contains("EMAIL:john@example.com"));
        assertTrue(firstVCard.contains("END:VCARD"));
        
        String secondVCard = vcards.get(1);
        assertTrue(secondVCard.contains("BEGIN:VCARD"));
        assertTrue(secondVCard.contains("FN:Jane Smith"));
        assertTrue(secondVCard.contains("EMAIL:jane@example.com"));
        assertTrue(secondVCard.contains("END:VCARD"));
    }

    @Test
    void testFetchVCardsFromWorkAddressbook() {
        Addressbook addressbook = new Addressbook("Work", "https://caldav.example.com/work/");
        String username = "testuser";
        String password = "testpass";
        
        List<String> vcards = caldavService.fetchVCards(addressbook, username, password);
        
        assertNotNull(vcards);
        assertEquals(1, vcards.size());
        
        String vcard = vcards.get(0);
        assertTrue(vcard.contains("BEGIN:VCARD"));
        assertTrue(vcard.contains("FN:Bob Johnson"));
        assertTrue(vcard.contains("EMAIL:bob@company.com"));
        assertTrue(vcard.contains("ORG:Acme Corp"));
        assertTrue(vcard.contains("END:VCARD"));
    }

    @Test
    void testFetchVCardsFromUnknownAddressbook() {
        Addressbook addressbook = new Addressbook("Unknown", "https://caldav.example.com/unknown/");
        String username = "testuser";
        String password = "testpass";
        
        List<String> vcards = caldavService.fetchVCards(addressbook, username, password);
        
        assertNotNull(vcards);
        assertTrue(vcards.isEmpty());
    }

    @Test
    void testFetchVCardsWithNullAddressbook() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(null, "user", "pass"));
    }

    @Test
    void testFetchVCardsWithAddressbookWithNullUrl() {
        Addressbook addressbook = new Addressbook("Test", null);
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(addressbook, "user", "pass"));
    }

    @Test
    void testFetchVCardsWithInvalidCredentials() {
        Addressbook addressbook = new Addressbook("Personal", "https://caldav.example.com/personal/");
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(addressbook, null, "pass"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(addressbook, "user", null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(addressbook, "", "pass"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.fetchVCards(addressbook, "user", ""));
    }

    @Test
    void testCreateAuthHeader() {
        String username = "testuser";
        String password = "testpass";
        
        String authHeader = caldavService.createAuthHeader(username, password);
        
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
        
        // Decode and verify
        String encoded = authHeader.substring(6); // Remove "Basic "
        String decoded = new String(java.util.Base64.getDecoder().decode(encoded));
        assertEquals("testuser:testpass", decoded);
    }

    @Test
    void testCreateAuthHeaderWithSpecialCharacters() {
        String username = "user@domain.com";
        String password = "p@ss:w0rd";
        
        String authHeader = caldavService.createAuthHeader(username, password);
        
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
        
        String encoded = authHeader.substring(6);
        String decoded = new String(java.util.Base64.getDecoder().decode(encoded));
        assertEquals("user@domain.com:p@ss:w0rd", decoded);
    }

    @Test
    void testCreateAuthHeaderWithInvalidCredentials() {
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.createAuthHeader(null, "pass"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.createAuthHeader("user", null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.createAuthHeader("", "pass"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            caldavService.createAuthHeader("user", ""));
    }
}