package me.bmordue.redweed.util;

import me.bmordue.redweed.exception.EpubParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EpubParserTest {

    @Test
    void testParseEpub(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("test.epub");
        Files.write(filePath, new byte[0]);
        File file = filePath.toFile();
        assertThrows(EpubParserException.class, () -> EpubParser.parse(file));
    }
}
