package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.service.PersonService;

/**
 * Controller for handling persons.
 */
@Controller("/persons")
public class PersonController {

    private final PersonService personService;

    /**
     * Constructor.
     *
     * @param personService the person service
     */
    @Inject
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Ingest a vCard.
     *
     * @param body the vCard body
     * @return the response
     */
    @Post
    public IngestVCardResponseDto ingestVCard(@Body String body) {
        return personService.ingestVCard(body);
    }
}