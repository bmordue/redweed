package me.bmordue.redweed.exception;

import java.io.IOException;

public class EpubParserException extends RuntimeException {
    public EpubParserException(String s, IOException e) {
        super(s, e);
    }
}
