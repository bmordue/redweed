package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.*;
import me.bmordue.redweed.service.PersonService;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;

@Controller()
public class PersonController {

    @Inject
    private PersonService personService;

    @Post
    public IngestVCardResponseDto ingestVCard(@Body String body) {
        return personService.ingestVCard(body);
    }
}