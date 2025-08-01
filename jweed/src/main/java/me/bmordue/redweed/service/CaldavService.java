package me.bmordue.redweed.service;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.Addressbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Service for interacting with CalDAV servers to fetch addressbook data.
 * This is a simplified implementation for testing purposes.
 */
@Singleton
public class CaldavService {

    private static final Logger log = LoggerFactory.getLogger(CaldavService.class);

    private final HttpClient httpClient;

    public CaldavService(@Client HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Discovers addressbooks available on the CalDAV server.
     * This is a simplified implementation that returns mock data for testing.
     *
     * @param caldavUrl the CalDAV server URL
     * @param username  the username for authentication
     * @param password  the password for authentication
     * @return list of discovered addressbooks
     */
    public List<Addressbook> discoverAddressbooks(String caldavUrl, String username, String password) {
        log.info("Discovering addressbooks from {}", caldavUrl);

        validateCredentials(username, password);
        validateUrl(caldavUrl);

        // For testing purposes, return mock addressbooks
        List<Addressbook> addressbooks = new ArrayList<>();
        addressbooks.add(new Addressbook("Personal", caldavUrl + "/addressbooks/personal/",
                "Personal contacts", "Personal Addressbook"));
        addressbooks.add(new Addressbook("Work", caldavUrl + "/addressbooks/work/",
                "Work contacts", "Work Addressbook"));

        return addressbooks;
    }

    /**
     * Fetches vCard data from a specific addressbook.
     * This is a simplified implementation that returns mock data for testing.
     *
     * @param addressbook the addressbook to fetch from
     * @param username    the username for authentication
     * @param password    the password for authentication
     * @return list of vCard strings
     */
    public List<String> fetchVCards(Addressbook addressbook, String username, String password) {
        log.info("Fetching vCards from addressbook: {}", addressbook != null ? addressbook.getName() : "null");

        validateCredentials(username, password);

        if (addressbook == null) {
            throw new IllegalArgumentException("Addressbook cannot be null");
        }

        if (addressbook.getUrl() == null) {
            throw new IllegalArgumentException("Addressbook URL cannot be null");
        }

        // For testing purposes, return mock vCard data
        List<String> vcards = new ArrayList<>();

        if ("Personal".equals(addressbook.getName())) {
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:John Doe
                    N:Doe;John;;;
                    EMAIL:john@example.com
                    TEL:+1-555-555-1234
                    END:VCARD
                    """);
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:Jane Smith
                    N:Smith;Jane;;;
                    EMAIL:jane@example.com
                    TEL:+1-555-555-5678
                    END:VCARD
                    """);
        } else if ("Work".equals(addressbook.getName())) {
            vcards.add("""
                    BEGIN:VCARD
                    VERSION:4.0
                    FN:Bob Johnson
                    N:Johnson;Bob;;;
                    EMAIL:bob@company.com
                    TEL:+1-555-555-9999
                    ORG:Acme Corp
                    END:VCARD
                    """);
        }

        return vcards;
    }

    /**
     * Creates HTTP Basic Authentication header.
     *
     * @param username the username
     * @param password the password
     * @return the authorization header value
     */
    public String createAuthHeader(String username, String password) {
        validateCredentials(username, password);
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }

    /**
     * Validates credentials.
     *
     * @param username the username
     * @param password the password
     */
    private void validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    /**
     * Validates URL.
     *
     * @param url the URL to validate
     */
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
    }


    public String findAddressbookHomeSetUrl(URI principalUrl, String username, String password) {
        String requestBody = """
                <d:propfind xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:carddav">
                  <d:prop>
                     <c:addressbook-home-set />
                  </d:prop>
                </d:propfind>
                """;

        HttpRequest<?> request = HttpRequest.create(HttpMethod.CUSTOM, principalUrl.toString(), "PROPFIND")
                .header("Depth", "0")
                .header("Content-Type", "application/xml")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .body(requestBody);

        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

        if (response.getStatus().getCode() >= 200 && response.getStatus().getCode() < 300) {
            return extractAddressbookHomeSetUrl(response.body());
        } else {
            throw new RuntimeException("Failed to find addressbook home set: " + response.getStatus());
        }
    }

    public List<Addressbook> findAddressbooks(URI addressbookHomeSetUrl, String username, String password) {
        String requestBody = """
                <d:propfind xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:carddav">
                  <d:prop>
                     <d:resourcetype />
                     <d:displayname />
                  </d:prop>
                </d:propfind>
                """;

        HttpRequest<?> request = HttpRequest.create(HttpMethod.CUSTOM, addressbookHomeSetUrl.toString(), "PROPFIND")
                .header("Depth", "1")
                .header("Content-Type", "application/xml")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .body(requestBody);

        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

        if (response.getStatus().getCode() >= 200 && response.getStatus().getCode() < 300) {
            return extractAddressbooks(response.body());
        } else {
            throw new RuntimeException("Failed to find addressbooks: " + response.getStatus());
        }
    }

    public List<String> getVCards(URI addressbookUrl, String username, String password, List<String> vCardUrls) {
        StringBuilder hrefElements = new StringBuilder();
        for (String vCardUrl : vCardUrls) {
            hrefElements.append("<d:href>").append(vCardUrl).append("</d:href>");
        }

        String requestBody = """
                <c:addressbook-multiget xmlns:d="DAV:" xmlns:c="urn:ietf:params:xml:ns:carddav">
                  <d:prop>
                    <d:getetag />
                    <c:address-data />
                  </d:prop>
                  %s
                </c:addressbook-multiget>
                """.formatted(hrefElements.toString());

        HttpRequest<?> request = HttpRequest.create(HttpMethod.CUSTOM, addressbookUrl.toString(), "PROPFIND")
                .header("Content-Type", "application/xml")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .body(requestBody);

        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

        if (response.getStatus().getCode() >= 200 && response.getStatus().getCode() < 300) {
            return extractVCards(response.body());
        } else {
            throw new RuntimeException("Failed to get vCards: " + response.getStatus());
        }
    }

    private String extractAddressbookHomeSetUrl(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            NodeList nodeList = doc.getElementsByTagNameNS("urn:ietf:params:xml:ns:carddav", "addressbook-home-set");
            if (nodeList.getLength() > 0 && nodeList.item(0) != null) {
                Element element = (Element) nodeList.item(0);
                if (element.getChildNodes().getLength() > 0 && element.getChildNodes().item(0) != null) {
                    return element.getChildNodes().item(0).getTextContent();
                }
            }
            throw new IllegalStateException("Addressbook home set URL not found in response");
        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {
            throw new RuntimeException("Failed to parse addressbook home set URL from response", e);
        }
    }

    private List<Addressbook> extractAddressbooks(String xmlResponse) {
        List<Addressbook> addressbooks = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            NodeList responseNodes = doc.getElementsByTagNameNS("DAV:", "response");
            for (int i = 0; i < responseNodes.getLength(); i++) {
                Element responseElement = (Element) responseNodes.item(i);
                NodeList resourceTypeNodes = responseElement.getElementsByTagNameNS("DAV:", "resourcetype");
                boolean isAddressbook = false;
                for (int j = 0; j < resourceTypeNodes.getLength(); j++) {
                    Element resourceTypeElement = (Element) resourceTypeNodes.item(j);
                    if (resourceTypeElement.getElementsByTagNameNS("urn:ietf:params:xml:ns:carddav", "addressbook").getLength() > 0) {
                        isAddressbook = true;
                        break;
                    }
                }

                if (isAddressbook) {
                    String href = responseElement.getElementsByTagNameNS("DAV:", "href").item(0).getTextContent();
                    NodeList displayNameNodes = responseElement.getElementsByTagNameNS("DAV:", "displayname");
                    String displayName = (displayNameNodes.getLength() > 0 && displayNameNodes.item(0) != null)
                            ? displayNameNodes.item(0).getTextContent()
                            : null; // or use a default value like "" if preferred
                    addressbooks.add(new Addressbook(URI.create(href), displayName));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse addressbooks from response", e);
        }
        return addressbooks;
    }

    private List<String> extractVCards(String xmlResponse) {
        List<String> vcards = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            NodeList vcardNodes = doc.getElementsByTagNameNS("urn:ietf:params:xml:ns:carddav", "address-data");
            for (int i = 0; i < vcardNodes.getLength(); i++) {
                vcards.add(vcardNodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse vCards from response", e);
        }
        return vcards;
    }
}
