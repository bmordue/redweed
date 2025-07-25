package me.bmordue.redweed.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Mp4Parser {

    public static final String TITLE = "title";
    public static final String CREATION_DATE = "creationDate";
    public static final String CREATION_TIME = "creation_time";
    private static final Logger log = LoggerFactory.getLogger(Mp4Parser.class);

    private Mp4Parser() {
        // hide public constructor
    }

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.start();
            Map<String, String> allMetadata = grabber.getMetadata();
            String title = allMetadata.get(TITLE);
            if (title != null) {
                metadata.put(TITLE, title);
            }
            parseCreationTime(metadata, allMetadata.get(CREATION_TIME));
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
        return metadata;
    }

    private static void parseCreationTime(Map<String, Object> metadata, String creationTime) {
        if (creationTime != null) {
            try {
                Instant instant = Instant.parse(creationTime);
                metadata.put(CREATION_DATE, instant);
            } catch (java.time.format.DateTimeParseException e) {
                log.warn("Failed to parse creation time as ISO 8601: {}", creationTime, e);
            }
        }
    }

    public static Optional<File> thumbnailFromFirstFrame(File file) {
        Optional<File> thumbnailFile = Optional.empty();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {

            grabber.start();

            // Grab the first frame
            Frame frame = grabber.grabImage();
            if (frame != null) {
                // Convert frame to BufferedImage
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);

                if (bufferedImage != null) {
                    thumbnailFile = Optional.of(File.createTempFile("thumbnail", ".png"));
                    ImageIO.write(bufferedImage, "png", thumbnailFile.get());
                }
                converter.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
        return thumbnailFile;
    }
}
