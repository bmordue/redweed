package me.bmordue.redweed.exception;

public class EpubParserException extends RuntimeException {
    public EpubParserException(String s, Exception e) {
        super(s, e);
    }
}
