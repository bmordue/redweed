package me.bmordue.redweed.controller;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Request DTO for importing vCards from CalDAV.
 */
@Serdeable
public class VCardImportRequest {

    private String caldavUrl;
    private String username;
    private String password;
    private String addressbookName;

    /**
     * Default constructor.
     */
    public VCardImportRequest() {
    }

    /**
     * Constructor with all required fields.
     *
     * @param caldavUrl       the CalDAV server URL
     * @param username        the username for authentication
     * @param password        the password for authentication
     * @param addressbookName the name of the addressbook to import
     */
    public VCardImportRequest(String caldavUrl, String username, String password, String addressbookName) {
        this.caldavUrl = caldavUrl;
        this.username = username;
        this.password = password;
        this.addressbookName = addressbookName;
    }

    /**
     * Get the CalDAV server URL.
     *
     * @return the CalDAV URL
     */
    public String getCaldavUrl() {
        return caldavUrl;
    }

    /**
     * Set the CalDAV server URL.
     *
     * @param caldavUrl the CalDAV URL
     */
    public void setCaldavUrl(String caldavUrl) {
        this.caldavUrl = caldavUrl;
    }

    /**
     * Get the username for authentication.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username for authentication.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password for authentication.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for authentication.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the addressbook name to import.
     *
     * @return the addressbook name
     */
    public String getAddressbookName() {
        return addressbookName;
    }

    /**
     * Set the addressbook name to import.
     *
     * @param addressbookName the addressbook name
     */
    public void setAddressbookName(String addressbookName) {
        this.addressbookName = addressbookName;
    }

    @Override
    public String toString() {
        return "VCardImportRequest{" +
                "caldavUrl='" + caldavUrl + '\'' +
                ", username='" + username + '\'' +
                ", password='***'" +
                ", addressbookName='" + addressbookName + '\'' +
                '}';
    }
}