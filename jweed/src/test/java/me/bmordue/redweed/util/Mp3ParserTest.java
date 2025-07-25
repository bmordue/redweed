package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Mp3ParserTest {

    String TEST_MP3 = "/catchy-jazzy-15-sec-stinger-343720.mp3";

    @Test
    void testParse() {
        File file = new File(getClass().getResource(TEST_MP3).getFile());
        if (file == null || !file.exists()) {
            throw new RuntimeException("Test file not found: " + TEST_MP3);
        }
        assertThrows(RuntimeException.class, () -> Mp3Parser.parse(file));
    }

    @Test
    
    void testParseSuccess() {
        File file = new File(getClass().getResource(TEST_MP3).getFile());
        if (file == null || !file.exists()) {
            throw new RuntimeException("Test file not found:" + TEST_MP3);
        }
        Map<String, String> metadata = Mp3Parser.parse(file);
        // TODO use correct expected values
        assertEquals("Test Title", metadata.get("title"));
        assertEquals("Test Artist", metadata.get("artist"));
        assertEquals("Test Album", metadata.get("album"));
        assertEquals("1", metadata.get("track"));
        assertEquals("Pop", metadata.get("genre"));
    }
}
