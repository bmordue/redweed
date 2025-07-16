package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.rdf.model.Model;

@Singleton
public class PersonService {

    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(String vCard) {
        Model model = VCardToRdfConverter.convert(vCard);
        personRepository.save(model);
        return new IngestVCardResponseDto("Success");
    }
}

