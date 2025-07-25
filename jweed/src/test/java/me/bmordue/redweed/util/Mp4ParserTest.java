package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.io.File;

class Mp4ParserTest extends UsesResourceTest {

    String testMp4 = "/14120146_2160_3840_30fps.mp4";

    @Test
    void testParse() {
        File file = getTestResource(testMp4);
        var metadata = Mp4Parser.parse(file);
        assert (!metadata.isEmpty());
    }

    @Test
    void testParseValidMp4GeneratesThumbnailMetadata() {
        File file = getTestResource(testMp4);

        var metadata = Mp4Parser.parse(file);

        assert (metadata.containsKey("thumbnail"));
        assert (metadata.get("thumbnail") instanceof File);
    }
}
