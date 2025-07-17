package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.domain.Person;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import me.bmordue.redweed.util.VCardParser;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;

import java.util.Map;
import java.util.UUID;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

@Singleton
public class PersonService {

    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(String vCard) {
        try {
            Model model = VCardToRdfConverter.convert(vCard);
            personRepository.save(model);
            return new IngestVCardResponseDto("Success");
        } catch (RuntimeException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid vCard");
        }
    }
}

