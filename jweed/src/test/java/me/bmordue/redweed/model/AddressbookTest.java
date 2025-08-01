package me.bmordue.redweed.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Addressbook model.
 */
class AddressbookTest {

    @Test
    void testDefaultConstructor() {
        Addressbook addressbook = new Addressbook();
        assertNull(addressbook.getName());
        assertNull(addressbook.getUrl());
        assertNull(addressbook.getDescription());
        assertNull(addressbook.getDisplayName());
    }

    @Test
    void testConstructorWithNameAndUrl() {
        String name = "Personal";
        String url = "https://example.com/personal/";
        
        Addressbook addressbook = new Addressbook(name, url);
        
        assertEquals(name, addressbook.getName());
        assertEquals(url, addressbook.getUrl());
        assertNull(addressbook.getDescription());
        assertNull(addressbook.getDisplayName());
    }

    @Test
    void testConstructorWithAllFields() {
        String name = "Work";
        String url = "https://example.com/work/";
        String description = "Work contacts";
        String displayName = "Work Addressbook";
        
        Addressbook addressbook = new Addressbook(name, url, description, displayName);
        
        assertEquals(name, addressbook.getName());
        assertEquals(url, addressbook.getUrl());
        assertEquals(description, addressbook.getDescription());
        assertEquals(displayName, addressbook.getDisplayName());
    }

    @Test
    void testSettersAndGetters() {
        Addressbook addressbook = new Addressbook();
        
        addressbook.setName("Family");
        addressbook.setUrl("https://example.com/family/");
        addressbook.setDescription("Family contacts");
        addressbook.setDisplayName("Family Addressbook");
        
        assertEquals("Family", addressbook.getName());
        assertEquals("https://example.com/family/", addressbook.getUrl());
        assertEquals("Family contacts", addressbook.getDescription());
        assertEquals("Family Addressbook", addressbook.getDisplayName());
    }

    @Test
    void testEqualsAndHashCode() {
        Addressbook addressbook1 = new Addressbook("Personal", "https://example.com/personal/");
        Addressbook addressbook2 = new Addressbook("Personal", "https://example.com/personal/");
        Addressbook addressbook3 = new Addressbook("Work", "https://example.com/work/");
        
        assertEquals(addressbook1, addressbook2);
        assertNotEquals(addressbook1, addressbook3);
        
        assertEquals(addressbook1.hashCode(), addressbook2.hashCode());
        assertNotEquals(addressbook1.hashCode(), addressbook3.hashCode());
    }

    @Test
    void testEqualsWithSameObject() {
        Addressbook addressbook = new Addressbook("Personal", "https://example.com/personal/");
        assertEquals(addressbook, addressbook);
    }

    @Test
    void testEqualsWithNull() {
        Addressbook addressbook = new Addressbook("Personal", "https://example.com/personal/");
        assertNotEquals(addressbook, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Addressbook addressbook = new Addressbook("Personal", "https://example.com/personal/");
        assertNotEquals(addressbook, "not an addressbook");
    }

    @Test
    void testEqualsWithDifferentName() {
        Addressbook addressbook1 = new Addressbook("Personal", "https://example.com/personal/");
        Addressbook addressbook2 = new Addressbook("Work", "https://example.com/personal/");
        assertNotEquals(addressbook1, addressbook2);
    }

    @Test
    void testEqualsWithDifferentUrl() {
        Addressbook addressbook1 = new Addressbook("Personal", "https://example.com/personal/");
        Addressbook addressbook2 = new Addressbook("Personal", "https://example.com/work/");
        assertNotEquals(addressbook1, addressbook2);
    }

    @Test
    void testToString() {
        Addressbook addressbook = new Addressbook("Personal", "https://example.com/personal/", 
                "Personal contacts", "Personal Addressbook");
        
        String toString = addressbook.toString();
        
        assertTrue(toString.contains("Personal"));
        assertTrue(toString.contains("https://example.com/personal/"));
        assertTrue(toString.contains("Personal contacts"));
        assertTrue(toString.contains("Personal Addressbook"));
    }

    @Test
    void testToStringWithNullValues() {
        Addressbook addressbook = new Addressbook();
        addressbook.setName("Test");
        addressbook.setUrl("https://example.com/test/");
        
        String toString = addressbook.toString();
        
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("https://example.com/test/"));
        assertFalse(toString.contains("null=null"));
    }
}