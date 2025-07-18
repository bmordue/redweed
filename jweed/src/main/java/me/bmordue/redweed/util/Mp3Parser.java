package me.bmordue.redweed.util;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Mp3Parser {

    public static Map<String, String> parse(File file) {
        Map<String, String> metadata = new HashMap<>();
        try {
            Mp3File mp3file = new Mp3File(file);
            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                metadata.put("title", id3v2Tag.getTitle());
                metadata.put("artist", id3v2Tag.getArtist());
                metadata.put("album", id3v2Tag.getAlbum());
                metadata.put("track", id3v2Tag.getTrack());
                metadata.put("genre", id3v2Tag.getGenreDescription());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MP3 file", e);
        }
        return metadata;
    }
}
