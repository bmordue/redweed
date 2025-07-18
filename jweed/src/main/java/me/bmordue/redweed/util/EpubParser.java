package me.bmordue.redweed.util;

import me.bmordue.redweed.exception.EpubParserException;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpubParser {

    private static final Logger log = LoggerFactory.getLogger(EpubParser.class);

    private EpubParser() {
        // Private constructor to prevent instantiation
    }

    public static Map<String, String> parse(File file) {
        log.info("Parsing file: {}", file.getName());
        Map<String, String> metadata = new HashMap<>();
        try (InputStream in = new FileInputStream(file)) {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(in);
            Metadata meta = book.getMetadata();
            if (!meta.getTitles().isEmpty()) {
                metadata.put("title", meta.getTitles().get(0));
            }
            if (!meta.getAuthors().isEmpty()) {
                metadata.put("creator", meta.getAuthors().get(0).getFirstname() + " " + meta.getAuthors().get(0).getLastname());
            }
            if (!meta.getPublishers().isEmpty()) {
                metadata.put("publisher", meta.getPublishers().get(0));
            }
            if (!meta.getDates().isEmpty()) {
                metadata.put("date", meta.getDates().get(0).getValue());
            }
        } catch (Exception e) {
            throw new EpubParserException("Error reading EPUB file: " + file.getName(), e);
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
