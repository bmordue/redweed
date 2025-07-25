package me.bmordue.redweed.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Mp3ParserTest extends UsesResourceTest {

    String testMp3 = "/catchy-jazzy-15-sec-stinger-343720.mp3";

    @Test
    void testParse() {
        File file = getTestResource(testMp3);
        Map<String, String> metadata = Mp3Parser.parse(file);
        assertNotNull(metadata);
    }

    @Test
    @Disabled("need a suitable test asset with valid metadata")
    void testParseSuccess() {
        File file = getTestResource(testMp3);
        Map<String, String> metadata = Mp3Parser.parse(file);
        // TODO use correct expected values
        assertEquals("Test Title", metadata.get("title"));
        assertEquals("Test Artist", metadata.get("artist"));
        assertEquals("Test Album", metadata.get("album"));
        assertEquals("1", metadata.get("track"));
        assertEquals("Pop", metadata.get("genre"));
    }
}
