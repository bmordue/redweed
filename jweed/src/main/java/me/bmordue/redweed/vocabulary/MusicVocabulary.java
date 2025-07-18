package me.bmordue.redweed.vocabulary;

import jakarta.inject.Singleton;

@Singleton
public class MusicVocabulary {
    public static final String MO_NS = "http://purl.org/ontology/mo/";
    public static final String MO_MUSICAL_WORK = MO_NS + "MusicalWork";
    public static final String MO_TITLE = MO_NS + "title";
    public static final String MO_ARTIST = MO_NS + "artist";
    public static final String MO_ALBUM = MO_NS + "album";
    public static final String MO_TRACK_NUMBER = MO_NS + "track_number";
    public static final String MO_GENRE = MO_NS + "genre";

    public String getWorkNamespace() {
        return "http://redweed.local/work/";
    }
}
