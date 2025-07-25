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
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    private static final Logger log = LoggerFactory.getLogger(Mp4Parser.class);

    private Mp4Parser() {
        // hide public constructor
    }

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

    public static File thumbnailFromFirstFrame(File file) {
        FFmpegFrameGrabber grabber = null;
        File thumbnailFile = null;
        try {
            grabber = new FFmpegFrameGrabber(file);
            grabber.start();

            // Grab the first frame
            Frame frame = grabber.grabImage();
            if (frame != null) {
                // Convert frame to BufferedImage
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);

                if (bufferedImage != null) {
                    thumbnailFile = File.createTempFile("thumbnail", ".png");

                    ImageIO.write(bufferedImage, "png", thumbnailFile);

                }
                converter.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    log.warn("Error stopping FFmpegFrameGrabber", e);
                }
            }
        }
        return thumbnailFile;
    }
}
