package me.bmordue.redweed.service;

import jakarta.inject.Singleton;
import me.bmordue.redweed.repository.PersonRepository;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;

@Singleton
public class PersonService {
    
    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(String vCard) {
        // Logic to process the VCard and return a response
        // TODO: complete this placeholder implementation
        throw new UnsupportedOperationException("Method not implemented yet");
    }

}

