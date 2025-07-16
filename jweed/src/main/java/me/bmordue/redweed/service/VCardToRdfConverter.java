package me.bmordue.redweed.service;

import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.VCardBuilder;
import net.fortuna.ical4j.vcard.property.N;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

import java.io.StringReader;
import java.util.UUID;

public final class VCardToRdfConverter {

    public static Model convert(String vCardString) {
        Model model = ModelFactory.createDefaultModel();
        try {
            VCardBuilder builder = new VCardBuilder(new StringReader(vCardString));
            VCard vcard = builder.build();

            String personUri = "urn:uuid:" + UUID.randomUUID();
            Resource person = model.createResource(personUri);

            Property fn = vcard.getProperty(Property.Id.FN);
            if (fn != null) {
                person.addProperty(VCARD.FN, fn.getValue());
            }

            N n = (N) vcard.getProperty(Property.Id.N);
            if (n != null) {
                person.addProperty(VCARD.N,
                    model.createResource()
                        .addProperty(VCARD.Family, n.getFamilyName())
                        .addProperty(VCARD.Given, n.getGivenName()));
            }

            vcard.getProperties(Property.Id.EMAIL).forEach(email -> person.addProperty(VCARD.EMAIL, email.getValue()));
            vcard.getProperties(Property.Id.TEL).forEach(tel -> person.addProperty(VCARD.TEL, tel.getValue()));
            vcard.getProperties(Property.Id.ADR).forEach(addr -> {
                String[] adrParts = addr.getValue().split(";");
                Resource address = model.createResource();
                address.addProperty(VCARD.Street, adrParts[2]);
                address.addProperty(VCARD.Locality, adrParts[3]);
                address.addProperty(VCARD.Region, adrParts[4]);
                address.addProperty(VCARD.Pcode, adrParts[5]);
                address.addProperty(VCARD.Country, adrParts[6]);
                person.addProperty(VCARD.ADR, address);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}
