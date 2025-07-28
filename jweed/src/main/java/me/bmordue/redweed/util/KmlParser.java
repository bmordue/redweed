package me.bmordue.redweed.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
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
factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
factory.setXIncludeAware(false);
factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(kmlString)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression placemarkExpr = xpath.compile("//Placemark");
            XPathExpression nameExpr = xpath.compile("./name");
            XPathExpression descExpr = xpath.compile("./description");
            XPathExpression coordinatesExpr = xpath.compile("./Point/coordinates");
            NodeList placemarkNodes = (NodeList) placemarkExpr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < placemarkNodes.getLength(); i++) {
                Node placemarkNode = placemarkNodes.item(i);
                Map<String, String> placemark = new HashMap<>();

                placemark.put("name", (String) nameExpr.evaluate(placemarkNode, XPathConstants.STRING));
                placemark.put("description", (String) descExpr.evaluate(placemarkNode, XPathConstants.STRING));

                String coordinatesStr = (String) coordinatesExpr.evaluate(placemarkNode, XPathConstants.STRING);
                if (coordinatesStr != null && !coordinatesStr.trim().isEmpty()) {
                    String[] coordinates = coordinatesStr.trim().split(",");
                    if (coordinates.length >= 2) {
                        placemark.put("longitude", coordinates[0].trim());
                        placemark.put("latitude", coordinates[1].trim());
                    }
                }
                placemarks.add(placemark);
            }
        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException |
                 javax.xml.xpath.XPathExpressionException e) {
            throw new IllegalArgumentException("Failed to parse KML string", e);
        }
        return placemarks;
    }
}
