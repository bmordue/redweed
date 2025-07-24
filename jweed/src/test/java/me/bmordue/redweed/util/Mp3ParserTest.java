package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Mp3ParserTest {

    @Test
    void testParse() {
        File file = new File(getClass().getClassLoader().getResource("test.mp3").getFile());
        assertThrows(RuntimeException.class, () -> Mp3Parser.parse(file));
    }
}
