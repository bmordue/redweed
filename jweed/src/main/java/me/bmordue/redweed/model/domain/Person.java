package me.bmordue.redweed.model.domain;

/**
 * A person.
 */
public class Person {
    private String uri;

    /**
     * Constructor.
     *
     * @param uri the URI of the person
     */
    public Person(String uri) {
        this.uri = uri;
    }

    /**
     * Get the URI of the person.
     *
     * @return the URI of the person
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the URI of the person.
     *
     * @param uri the URI of the person
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    
}
