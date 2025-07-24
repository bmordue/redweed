package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Mp4ParserTest {

    @Test
    void testParse() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("test.mp4").getFile());
        assertThrows(RuntimeException.class, () -> Mp4Parser.parse(file));
    }

    @Test
    void testParseValidMp4GeneratesThumbnailMetadata() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("test.mp4").getFile());
        var metadata = Mp4Parser.parse(file);
        // Check that the metadata contains a thumbnail key and it is a File
        assert(metadata.containsKey("thumbnail"));
        assert(metadata.get("thumbnail") instanceof File);
    }
}
