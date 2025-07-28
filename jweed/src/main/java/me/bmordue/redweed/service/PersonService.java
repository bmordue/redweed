package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for people.
 */
@Singleton
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    /**
     * Constructor.
     *
     * @param personRepository the person repository
     */
    @Inject
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Ingest a vCard.
     *
     * @param vCard the vCard content
     * @return the response
     */
    public IngestVCardResponseDto ingestVCard(String vCard) {
        Model model;
        try {
            model = VCardToRdfConverter.convert(vCard);
        } catch (RuntimeException e) {
            log.error("Invalid vCard", e);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid vCard");
        }

        personRepository.save(model);

        return new IngestVCardResponseDto("success");
    }
}

