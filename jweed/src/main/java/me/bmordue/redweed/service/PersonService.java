package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.rdf.model.Model;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

@Singleton
public class PersonService {

    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(String vCard) {
        Model model;
        try {
            model = VCardToRdfConverter.convert(vCard);
        } catch (RuntimeException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid vCard");
        }

        personRepository.save(model);

        return new IngestVCardResponseDto("success");
    }
}

