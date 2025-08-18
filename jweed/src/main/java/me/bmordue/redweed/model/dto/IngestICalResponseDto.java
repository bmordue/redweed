package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for the response of an iCal ingestion.
 */
@Serdeable
public class IngestICalResponseDto {
    private String status;
    private String message;
    private String eventUri;
    private int eventCount;

    /**
     * Default constructor.
     */
    public IngestICalResponseDto() {
        this.status = "success";
    }

    /**
     * Constructor with message.
     *
     * @param message the response message
     */
    public IngestICalResponseDto(String message) {
        this.status = "success";
        this.message = message;
    }

    /**
     * Constructor with message and event count.
     *
     * @param message    the response message
     * @param eventCount the number of events processed
     */
    public IngestICalResponseDto(String message, int eventCount) {
        this.status = "success";
        this.message = message;
        this.eventCount = eventCount;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the event URI.
     *
     * @return the event URI
     */
    public String getEventUri() {
        return eventUri;
    }

    /**
     * Sets the event URI.
     *
     * @param eventUri the event URI
     */
    public void setEventUri(String eventUri) {
        this.eventUri = eventUri;
    }

    /**
     * Gets the event count.
     *
     * @return the event count
     */
    public int getEventCount() {
        return eventCount;
    }

    /**
     * Sets the event count.
     *
     * @param eventCount the event count
     */
    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}
