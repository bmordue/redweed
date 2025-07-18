package me.bmordue.redweed.vocabulary;

import jakarta.inject.Singleton;

@Singleton
public class BookVocabulary {
    public static final String BIBO_NS = "http://purl.org/ontology/bibo/";
    public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
    public static final String BIBO_BOOK = BIBO_NS + "Book";
    public static final String DC_TITLE = DC_NS + "title";
    public static final String DC_CREATOR = DC_NS + "creator";
    public static final String DC_PUBLISHER = DC_NS + "publisher";
    public static final String DC_DATE = DC_NS + "date";

    public String getBookNamespace() {
        return "http://redweed.local/book/";
    }
}
