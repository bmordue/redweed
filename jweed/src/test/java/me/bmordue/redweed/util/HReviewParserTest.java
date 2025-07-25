package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HReviewParserTest {

    @Test
    void testParse() {
        String html = "<div class=\"hreview\">" +
                "<span class=\"rating\">5</span>" +
                "<span class=\"description\">This is a test review.</span>" +
                "<span class=\"reviewer\">Test Reviewer</span>" +
                "</div>";
        List<Map<String, String>> reviews = HReviewParser.parse(html);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        Map<String, String> review = reviews.get(0);
        assertEquals("5", review.get("rating"));
        assertEquals("This is a test review.", review.get("description"));
        assertEquals("Test Reviewer", review.get("reviewer"));
    }
}
