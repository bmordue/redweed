package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KmlParserTest {

    @Test
    void testParse() {
        String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">" +
                "<Placemark>" +
                "<name>Test Placemark</name>" +
                "<description>This is a test placemark.</description>" +
                "<Point>" +
                "<coordinates>-122.0822035425683,37.42228990140251,0</coordinates>" +
                "</Point>" +
                "</Placemark>" +
                "</kml>";
        List<Map<String, String>> placemarks = KmlParser.parse(kml);
        assertNotNull(placemarks);
        assertEquals(1, placemarks.size());
        Map<String, String> placemark = placemarks.get(0);
        assertEquals("Test Placemark", placemark.get("name"));
        assertEquals("This is a test placemark.", placemark.get("description"));
        assertEquals("-122.0822035425683", placemark.get("longitude"));
        assertEquals("37.42228990140251", placemark.get("latitude"));
    }
}
