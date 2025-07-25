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
        assertInstanceOf(File.class, metadata.get("thumbnail"));
    }
}
