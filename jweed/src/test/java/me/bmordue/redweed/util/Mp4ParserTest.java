package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class Mp4ParserTest extends UsesResourceTest {

    String testMp4 = "/14120146_2160_3840_30fps.mp4";

    @Test
    void testParse() {
        File file = getTestResource(testMp4);
        var metadata = Mp4Parser.parse(file);
        assertFalse(metadata.isEmpty());
    }

    @Test
    void testParseValidMp4GeneratesThumbnailMetadata() {
        File file = getTestResource(testMp4);

        var metadata = Mp4Parser.parse(file);

        assertTrue(metadata.containsKey("thumbnail"));
        Object thumbnailObj = metadata.get("thumbnail");
        assertInstanceOf(File.class, thumbnailObj);
        File thumbnailFile = (File) thumbnailObj;
        try {
            assertTrue(thumbnailFile.exists(), "Thumbnail file should exist.");
            assertTrue(thumbnailFile.length() > 0, "Thumbnail file should not be empty.");
        } finally {
            thumbnailFile.delete();
        }
    }
}
