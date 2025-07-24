package me.bmordue.redweed.util;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Mp4Parser {

    public static Map<String, Object> parse(File file) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            Picture picture = FrameGrab.getFrameFromFile(file, 0);
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            File thumbnailFile = File.createTempFile("thumbnail", ".png");
            try {
                ImageIO.write(bufferedImage, "png", thumbnailFile);
                metadata.put("thumbnail", thumbnailFile);
            } finally {
                thumbnailFile.delete();
            }
        } catch (IOException | JCodecException e) {
            throw new RuntimeException("Failed to parse MP4 file", e);
        }
        return metadata;
    }
}
