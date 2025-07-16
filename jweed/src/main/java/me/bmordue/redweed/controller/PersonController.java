package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.*;

@Controller()
public class PersonController {

    @Inject
    private PersonService personService;

    @Post
    public IngestVCardResponseDto ingestVCard(@Body VCard body) {
        return personService.ingestVCard(body);
    }
}