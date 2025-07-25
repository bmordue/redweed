package me.bmordue.redweed.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.start();
            Map<String, String> allMetadata = grabber.getMetadata();
            metadata.put("title", allMetadata.get("title"));
            String creationTime = allMetadata.get("creation_time");
            if (creationTime != null) {
                // FFmpeg creation_time is in ISO 8601 format, e.g., "2024-07-25T04:43:56.000000Z"
                Instant instant = Instant.parse(creationTime);
                metadata.put("creationDate", Date.from(instant));
            }
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
        return metadata;
    }
}
