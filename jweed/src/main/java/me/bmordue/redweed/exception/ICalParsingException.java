package me.bmordue.redweed.exception;

public class ICalParsingException extends RuntimeException {
    public ICalParsingException(String message, Exception e) {
        super(message, e);
    }
}
