package me.bmordue.redweed.exception;

/**
 * Exception thrown when an iCal file cannot be parsed.
 */
public class ICalParsingException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message the message
     * @param e the cause
     */
    public ICalParsingException(String message, Exception e) {
        super(message, e);
    }
}
