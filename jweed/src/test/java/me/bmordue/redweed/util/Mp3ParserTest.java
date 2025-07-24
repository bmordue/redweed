package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Mp3ParserTest {

    @Test
    void testParse() {
        File file = new File(getClass().getClassLoader().getResource("test.mp3").getFile());
        assertThrows(RuntimeException.class, () -> Mp3Parser.parse(file));
    }

    @Test
    void testParseSuccess() {
        File file = new File(getClass().getClassLoader().getResource("test.mp3").getFile());
        Map<String, String> metadata = Mp3Parser.parse(file);
        // Replace these expected values with the actual metadata in your test.mp3
        assertEquals("Test Title", metadata.get("title"));
        assertEquals("Test Artist", metadata.get("artist"));
        assertEquals("Test Album", metadata.get("album"));
        assertEquals("1", metadata.get("track"));
        assertEquals("Pop", metadata.get("genre"));
    }
}
