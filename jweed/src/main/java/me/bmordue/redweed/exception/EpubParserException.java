package me.bmordue.redweed.exception;

/**
 * Exception thrown when an EPUB file cannot be parsed.
 */
public class EpubParserException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param s the message
     * @param e the cause
     */
    public EpubParserException(String s, Exception e) {
        super(s, e);
    }
}
