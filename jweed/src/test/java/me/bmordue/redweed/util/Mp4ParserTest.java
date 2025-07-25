package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Mp4ParserTest {

    String TEST_MP4 = "/14120146_2160_3840_30fps.mp4";

    @Test
    void testParse() throws IOException {
        File file = new File(getClass().getResource(TEST_MP4).getFile());
        if (file == null || !file.exists()) {
            throw new RuntimeException("Test file not found: " + TEST_MP4);
        }
        assertThrows(RuntimeException.class, () -> Mp4Parser.parse(file));
    }

    @Test
    void testParseValidMp4GeneratesThumbnailMetadata() throws IOException {
        File file = new File(getClass().getResource(TEST_MP4).getFile());
                if (file == null || !file.exists()) {
            throw new RuntimeException("Test file not found: " + TEST_MP4);
        }

        var metadata = Mp4Parser.parse(file);
        // Check that the metadata contains a thumbnail key and it is a File
        assert(metadata.containsKey("thumbnail"));
        assert(metadata.get("thumbnail") instanceof File);
    }
}
