package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.service.PersonService;

@Controller("/persons")
public class PersonController {

    @Inject
    private PersonService personService;

    @Post
    public IngestVCardResponseDto ingestVCard(@Body String body) {
        return personService.ingestVCard(body);
    }
}