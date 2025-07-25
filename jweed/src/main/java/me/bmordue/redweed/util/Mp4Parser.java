package me.bmordue.redweed.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        FFmpegFrameGrabber grabber = null;
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
                    File thumbnailFile = File.createTempFile("thumbnail", ".png");
                    try {
                        ImageIO.write(bufferedImage, "png", thumbnailFile);
                        metadata.put("thumbnail", thumbnailFile);
                    } finally {
                        thumbnailFile.delete();
                    }
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
                    // Log but don't throw
                }
            }
        }
        return metadata;
    }
}
