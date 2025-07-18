package me.bmordue.redweed.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            throw new IOException("Not yet implemented");
//            Movie movie = MovieCreator.build(file.getAbsolutePath());
//            metadata.put("title", movie.getMovieMetaData().getTitle());
//            metadata.put("creationDate", movie.getMovieMetaData().getCreationTime());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
//        return metadata;
    }
}
