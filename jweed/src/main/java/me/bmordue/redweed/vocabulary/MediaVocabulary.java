package me.bmordue.redweed.vocabulary;

import jakarta.inject.Singleton;

@Singleton
public class MediaVocabulary {

    public static final String MA_NS = "http://www.w3.org/ns/ma-ont/";
    public static final String MA_MEDIA_RESOURCE = MA_NS + "MediaResource";
    public static final String MA_TITLE = MA_NS + "title";
    public static final String MA_CREATION_DATE = MA_NS + "creationDate";
    public static final String MA_CANONICAL_LOCATION = MA_NS + "canonicalLocation";

    public String getResourceNamespace() {
        return "http://redweed.local/resource/";
    }
}
