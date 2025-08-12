package me.bmordue.redweed.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for VCardImportRequest DTO.
 */
class VCardImportRequestTest {

    @Test
    void testDefaultConstructor() {
        VCardImportRequest request = new VCardImportRequest();
        assertNull(request.getCaldavUrl());
        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getAddressbookName());
    }

    @Test
    void testConstructorWithAllFields() {
        String caldavUrl = "https://caldav.example.com";
        String username = "testuser";
        String password = "testpass";
        String addressbookName = "Personal";
        
        VCardImportRequest request = new VCardImportRequest(caldavUrl, username, password, addressbookName);
        
        assertEquals(caldavUrl, request.getCaldavUrl());
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(addressbookName, request.getAddressbookName());
    }

    @Test
    void testSettersAndGetters() {
        VCardImportRequest request = new VCardImportRequest();
        
        request.setCaldavUrl("https://caldav.example.com");
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setAddressbookName("Work");
        
        assertEquals("https://caldav.example.com", request.getCaldavUrl());
        assertEquals("testuser", request.getUsername());
        assertEquals("testpass", request.getPassword());
        assertEquals("Work", request.getAddressbookName());
    }

    @Test
    void testToStringMasksPassword() {
        VCardImportRequest request = new VCardImportRequest(
                "https://caldav.example.com", 
                "testuser", 
                "secretpassword", 
                "Personal"
        );
        
        String toString = request.toString();
        
        assertTrue(toString.contains("https://caldav.example.com"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("Personal"));
        assertTrue(toString.contains("***"));
        assertFalse(toString.contains("secretpassword"));
    }

    @Test
    void testToStringWithNullPassword() {
        VCardImportRequest request = new VCardImportRequest();
        request.setCaldavUrl("https://caldav.example.com");
        request.setUsername("testuser");
        request.setAddressbookName("Personal");
        // password remains null
        
        String toString = request.toString();
        
        assertTrue(toString.contains("https://caldav.example.com"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("Personal"));
        assertTrue(toString.contains("***"));
    }

    @Test
    void testFieldsCanBeUpdated() {
        VCardImportRequest request = new VCardImportRequest(
                "https://old.example.com", 
                "olduser", 
                "oldpass", 
                "OldBook"
        );
        
        // Update all fields
        request.setCaldavUrl("https://new.example.com");
        request.setUsername("newuser");
        request.setPassword("newpass");
        request.setAddressbookName("NewBook");
        
        assertEquals("https://new.example.com", request.getCaldavUrl());
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass", request.getPassword());
        assertEquals("NewBook", request.getAddressbookName());
    }

    @Test
    void testWithEmptyStrings() {
        VCardImportRequest request = new VCardImportRequest("", "", "", "");
        
        assertEquals("", request.getCaldavUrl());
        assertEquals("", request.getUsername());
        assertEquals("", request.getPassword());
        assertEquals("", request.getAddressbookName());
    }

    @Test
    void testSerializability() {
        // Test that the class can be constructed and has proper getters/setters
        // which are required for serialization frameworks like Jackson
        VCardImportRequest request = new VCardImportRequest();
        
        // Test setting and getting each field individually
        request.setCaldavUrl("https://test.com");
        assertNotNull(request.getCaldavUrl());
        
        request.setUsername("user");
        assertNotNull(request.getUsername());
        
        request.setPassword("pass");
        assertNotNull(request.getPassword());
        
        request.setAddressbookName("book");
        assertNotNull(request.getAddressbookName());
    }
}