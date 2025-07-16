package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.domain.Person;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import me.bmordue.redweed.util.VCardParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;

import java.util.Map;
import java.util.UUID;

@Singleton
public class PersonService {

    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(String vCardString) {
        Map<String, String> vCardMap = VCardParser.parse(vCardString);
        String fn = vCardMap.get("FN");
        String email = vCardMap.get("EMAIL");

        String personUri = "http://redweed.local/person/" + UUID.randomUUID();

        Model model = ModelFactory.createDefaultModel();
        Resource personResource = model.createResource(personUri)
                .addProperty(RDF.type, VCARD.Individual)
                .addProperty(VCARD.FN, fn);

        if (email != null) {
            personResource.addProperty(VCARD.EMAIL, email);
        }

        personRepository.save(model);

        return new IngestVCardResponseDto(personUri, "vCard ingested successfully");
    }
}

