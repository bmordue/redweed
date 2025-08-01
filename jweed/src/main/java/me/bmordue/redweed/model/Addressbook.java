package me.bmordue.redweed.model;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Represents a CalDAV addressbook containing vCard data.
 */
@Serdeable
public class Addressbook {

    private String name;
    private String url;
    private String description;
    private String displayName;

    /**
     * Default constructor.
     */
    public Addressbook() {
    }

    /**
     * Constructor with name and URL.
     *
     * @param name the addressbook name
     * @param url  the addressbook URL
     */
    public Addressbook(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Constructor with all fields.
     *
     * @param name        the addressbook name
     * @param url         the addressbook URL
     * @param description the addressbook description
     * @param displayName the addressbook display name
     */
    public Addressbook(String name, String url, String description, String displayName) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.displayName = displayName;
    }

    /**
     * Get the addressbook name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the addressbook name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the addressbook URL.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the addressbook URL.
     *
     * @param url the URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the addressbook description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the addressbook description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the addressbook display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the addressbook display name.
     *
     * @param displayName the display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Addressbook that = (Addressbook) o;

        if (!name.equals(that.name)) return false;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Addressbook{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}