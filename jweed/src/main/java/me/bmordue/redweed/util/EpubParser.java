package me.bmordue.redweed.util;

import me.bmordue.redweed.exception.EpubParserException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EpubParser {

    private EpubParser() {
        // Private constructor to prevent instantiation
    }

    public static Map<String, String> parse(File file) {
        Map<String, String> metadata = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             ZipArchiveInputStream zip = new ZipArchiveInputStream(fis)) {

            ZipArchiveEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".opf") || entry.getName().contains("content.opf")) {
                    // Parse the OPF file for metadata
                    parseOpfMetadata(zip, metadata);
                    break;
                }
            }
        } catch (java.io.IOException e) {
            throw new EpubParserException("Error reading EPUB file: " + file.getName(), e);
        }

        return metadata;
    }

    private static void parseOpfMetadata(InputStream opfStream, Map<String, String> metadata) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Secure the XML parser against XXE attacks
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(opfStream);

            Element root = doc.getDocumentElement();

            // Parse Dublin Core metadata
            NodeList titleNodes = root.getElementsByTagName("dc:title");
            if (titleNodes.getLength() > 0) {
                metadata.put("title", titleNodes.item(0).getTextContent().trim());
            }

            NodeList creatorNodes = root.getElementsByTagName("dc:creator");
            if (creatorNodes.getLength() > 0) {
                metadata.put("creator", creatorNodes.item(0).getTextContent().trim());
            }

            NodeList publisherNodes = root.getElementsByTagName("dc:publisher");
            if (publisherNodes.getLength() > 0) {
                metadata.put("publisher", publisherNodes.item(0).getTextContent().trim());
            }

            NodeList dateNodes = root.getElementsByTagName("dc:date");
            if (dateNodes.getLength() > 0) {
                metadata.put("date", dateNodes.item(0).getTextContent().trim());
            }

            NodeList identifierNodes = root.getElementsByTagName("dc:identifier");
            if (identifierNodes.getLength() > 0) {
                metadata.put("identifier", identifierNodes.item(0).getTextContent().trim());
            }

            NodeList languageNodes = root.getElementsByTagName("dc:language");
            if (languageNodes.getLength() > 0) {
                metadata.put("language", languageNodes.item(0).getTextContent().trim());
            }

        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {
            throw new EpubParserException("Error parsing OPF metadata", e);
        }
    }

}
