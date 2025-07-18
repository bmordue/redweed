package me.bmordue.redweed.util;

//import com.adobe.epubcheck.api.EpubCheck;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EpubParser {

    public static Map<String, String> parse(File file) {
        Map<String, String> metadata = new HashMap<>();
        try {
//            EpubCheck epubCheck = new EpubCheck(file);
//            com.adobe.epubcheck.opf.OPFResource opf = epubCheck.getPackage().getOpfResource();
//            try (InputStream in = opf.getInputStream()) {
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document doc = builder.parse(in);
//                NodeList metadataNodes = doc.getElementsByTagName("metadata");
//                if (metadataNodes.getLength() > 0) {
//                    Element metadataElement = (Element) metadataNodes.item(0);
//                    metadata.put("title", getTagValue("dc:title", metadataElement));
//                    metadata.put("creator", getTagValue("dc:creator", metadataElement));
//                    metadata.put("publisher", getTagValue("dc:publisher", metadataElement));
//                    metadata.put("date", getTagValue("dc:date", metadataElement));
//                }
//            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse EPUB file", e);
        }
        return metadata;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
