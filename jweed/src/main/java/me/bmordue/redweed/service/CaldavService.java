package me.bmordue.redweed.service;

import jakarta.inject.Singleton;
import me.bmordue.redweed.model.Addressbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Service for interacting with CalDAV servers to fetch addressbook data.
 * This is a simplified implementation for testing purposes.
 */
@Singleton
public class CaldavService {

    private static final Logger log = LoggerFactory.getLogger(CaldavService.class);

    /**
     * Discovers addressbooks available on the CalDAV server.
     * This is a simplified implementation that returns mock data for testing.
     *
     * @param caldavUrl the CalDAV server URL
     * @param username  the username for authentication
     * @param password  the password for authentication
     * @return list of discovered addressbooks
     */
    public List<Addressbook> discoverAddressbooks(String caldavUrl, String username, String password) {
        log.info("Discovering addressbooks from {}", caldavUrl);
        
        validateCredentials(username, password);
        validateUrl(caldavUrl);
        
        // For testing purposes, return mock addressbooks
        List<Addressbook> addressbooks = new ArrayList<>();
        addressbooks.add(new Addressbook("Personal", caldavUrl + "/addressbooks/personal/", 
                "Personal contacts", "Personal Addressbook"));
        addressbooks.add(new Addressbook("Work", caldavUrl + "/addressbooks/work/", 
                "Work contacts", "Work Addressbook"));
        
        return addressbooks;
    }

    /**
     * Fetches vCard data from a specific addressbook.
     * This is a simplified implementation that returns mock data for testing.
     *
     * @param addressbook the addressbook to fetch from
     * @param username    the username for authentication
     * @param password    the password for authentication
     * @return list of vCard strings
     */
    public List<String> fetchVCards(Addressbook addressbook, String username, String password) {
        log.info("Fetching vCards from addressbook: {}", addressbook != null ? addressbook.getName() : "null");
        
        validateCredentials(username, password);
        
        if (addressbook == null) {
            throw new IllegalArgumentException("Addressbook cannot be null");
        }
        
        if (addressbook.getUrl() == null) {
            throw new IllegalArgumentException("Addressbook URL cannot be null");
        }
        
        // For testing purposes, return mock vCard data
        List<String> vcards = new ArrayList<>();
        
        if ("Personal".equals(addressbook.getName())) {
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:John Doe
                    N:Doe;John;;;
                    EMAIL:john@example.com
                    TEL:+1-555-555-1234
                    END:VCARD
                    """);
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:Jane Smith
                    N:Smith;Jane;;;
                    EMAIL:jane@example.com
                    TEL:+1-555-555-5678
                    END:VCARD
                    """);
        } else if ("Work".equals(addressbook.getName())) {
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:Bob Johnson
                    N:Johnson;Bob;;;
                    EMAIL:bob@company.com
                    TEL:+1-555-555-9999
                    ORG:Acme Corp
                    END:VCARD
                    """);
        }
        
        return vcards;
    }

    /**
     * Creates HTTP Basic Authentication header.
     *
     * @param username the username
     * @param password the password
     * @return the authorization header value
     */
    public String createAuthHeader(String username, String password) {
        validateCredentials(username, password);
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }

    /**
     * Validates credentials.
     *
     * @param username the username
     * @param password the password
     */
    private void validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    /**
     * Validates URL.
     *
     * @param url the URL to validate
     */
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
    }
}