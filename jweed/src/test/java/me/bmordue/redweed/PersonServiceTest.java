package me.bmordue.redweed;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class PersonServiceTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    Dataset dataset;

    @Test
    void testVCardIngest() {
        String vcard = """
            BEGIN:VCARD
            VERSION:4.0
            FN:John Doe
            N:Doe;John;;;
            EMAIL;TYPE=work:johndoe@example.com
            TEL;TYPE=work,voice;VALUE=uri:tel:+1-555-555-5555
            ADR;TYPE=work:;;123 Main St;Anytown;CA;12345;USA
            END:VCARD
            """;

        IngestVCardResponseDto response = client.toBlocking()
            .retrieve(HttpRequest.POST("/persons", vcard), IngestVCardResponseDto.class);

        assertEquals("Success", response.getMessage());

        dataset.begin(ReadWrite.READ);
        try {
            Model model = dataset.getDefaultModel();
            Resource person = model.listResourcesWithProperty(VCARD.FN, "John Doe").nextResource();
            assertTrue(person.hasProperty(VCARD.N));
            Resource n = person.getProperty(VCARD.N).getResource();
            assertEquals("Doe", n.getProperty(VCARD.Family).getString());
            assertEquals("John", n.getProperty(VCARD.Given).getString());
            assertEquals("johndoe@example.com", person.getProperty(VCARD.EMAIL).getString());
            assertEquals("tel:+1-555-555-5555", person.getProperty(VCARD.TEL).getString());
            Resource adr = person.getProperty(VCARD.ADR).getResource();
            assertEquals("123 Main St", adr.getProperty(VCARD.Street).getString());
            assertEquals("Anytown", adr.getProperty(VCARD.Locality).getString());
            assertEquals("CA", adr.getProperty(VCARD.Region).getString());
            assertEquals("12345", adr.getProperty(VCARD.Pcode).getString());
            assertEquals("USA", adr.getProperty(VCARD.Country).getString());
        } finally {
            dataset.end();
        }
    }
}
