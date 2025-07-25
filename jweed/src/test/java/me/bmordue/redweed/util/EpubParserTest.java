package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpubParserTest {

    @Test
    void testParseEpub() throws IOException {
        // Create a test epub file
        File epubFile = File.createTempFile("test", ".epub");
        epubFile.deleteOnExit();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(epubFile))) {
            ZipEntry entry = new ZipEntry("content.opf");
            zos.putNextEntry(entry);
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("test.opf")) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
            zos.closeEntry();
        }

        // Parse the epub file
        Map<String, String> metadata = EpubParser.parse(epubFile);

        // Verify the metadata
        assertNotNull(metadata);
        assertEquals("Test Title", metadata.get("title"));
        assertEquals("Test Author", metadata.get("creator"));
        assertEquals("Test Publisher", metadata.get("publisher"));
        assertEquals("2024-01-01", metadata.get("date"));
        assertEquals("urn:uuid:12345", metadata.get("identifier"));
        assertEquals("en", metadata.get("language"));
    }
}
