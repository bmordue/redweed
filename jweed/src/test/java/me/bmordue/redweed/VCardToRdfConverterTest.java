package me.bmordue.redweed;

import me.bmordue.redweed.service.VCardToRdfConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VCardToRdfConverterTest {

    @Test
    void testConvertVCardToModel() {
        String vcardString = """
            BEGIN:VCARD
            VERSION:4.0
            FN:John Doe
            N:Doe;John;;;
            EMAIL;TYPE=work:johndoe@example.com
            TEL;TYPE=work,voice;VALUE=uri:tel:+1-555-555-5555
            ADR;TYPE=work:;;123 Main St;Anytown;CA;12345;USA
            END:VCARD
            """;

Model model = VCardToRdfConverter.convert(vcardString);
        Model model = converter.convert(vcardString);

        assertNotNull(model);

        Resource resource = model.listResourcesWithProperty(VCARD.FN, "John Doe").nextResource();
        assertNotNull(resource);

        assertEquals("Doe", resource.getProperty(VCARD.N).getResource().getProperty(VCARD.Family).getString());
        assertEquals("John", resource.getProperty(VCARD.N).getResource().getProperty(VCARD.Given).getString());
        assertEquals("johndoe@example.com", resource.getProperty(VCARD.EMAIL).getString());
        assertEquals("tel:+1-555-555-5555", resource.getProperty(VCARD.TEL).getLiteral().getLexicalForm());
        assertEquals("123 Main St", resource.getProperty(VCARD.ADR).getResource().getProperty(VCARD.Street).getString());
        assertEquals("Anytown", resource.getProperty(VCARD.ADR).getResource().getProperty(VCARD.Locality).getString());
        assertEquals("CA", resource.getProperty(VCARD.ADR).getResource().getProperty(VCARD.Region).getString());
        assertEquals("12345", resource.getProperty(VCARD.ADR).getResource().getProperty(VCARD.Pcode).getString());
        assertEquals("USA", resource.getProperty(VCARD.ADR).getResource().getProperty(VCARD.Country).getString());
    }

    @Test
    void testConvertInvalidVCardToModel() {
        String vcardString = "this is not a vcard";

assertThrows(RuntimeException.class, () -> VCardToRdfConverter.convert(vcardString));
        assertThrows(RuntimeException.class, () -> converter.convert(vcardString));
    }
}
