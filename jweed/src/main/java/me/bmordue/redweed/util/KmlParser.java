package me.bmordue.redweed.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmlParser {

    public static List<Map<String, String>> parse(String kmlString) {
        List<Map<String, String>> placemarks = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(kmlString)));
            NodeList placemarkNodes = doc.getElementsByTagName("Placemark");
            for (int i = 0; i < placemarkNodes.getLength(); i++) {
                Node placemarkNode = placemarkNodes.item(i);
                if (placemarkNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element placemarkElement = (Element) placemarkNode;
                    Map<String, String> placemark = new HashMap<>();
                    placemark.put("name", getTagValue("name", placemarkElement));
                    placemark.put("description", getTagValue("description", placemarkElement));
                    NodeList coordinatesNodes = placemarkElement.getElementsByTagName("coordinates");
                    if (coordinatesNodes.getLength() > 0) {
                        String[] coordinates = coordinatesNodes.item(0).getTextContent().trim().split(",");
                        if (coordinates.length >= 2) {
                            placemark.put("longitude", coordinates[0].trim());
                            placemark.put("latitude", coordinates[1].trim());
                        }                    }
                    placemarks.add(placemark);
                }
            }
        } catch (Exception e) {
            // Consider using a logger: log.error("Failed to parse KML string", e);
            throw new RuntimeException("Failed to parse KML string", e);
        }        return placemarks;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.hasChildNodes()) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return ""; // Return empty string or null for missing tags
    }
}
