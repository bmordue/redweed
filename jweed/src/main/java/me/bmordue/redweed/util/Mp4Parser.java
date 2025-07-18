package me.bmordue.redweed.util;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            Movie movie = MovieCreator.build(file.getAbsolutePath());
            metadata.put("title", movie.getMovieMetaData().getTitle());
            metadata.put("creationDate", movie.getMovieMetaData().getCreationTime());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
        return metadata;
    }
}
