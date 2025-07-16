package me.bmordue.redweed.service;

import jakarta.inject.Singleton;

@Singleton
public class PersonService {
    
    @Inject
    private PersonRepository personRepository;

    public IngestVCardResponseDto ingestVCard(VCard vCard) {
        // Logic to process the VCard and return a response
        // TODO: complete this placeholder implementation
        throw new UnsupportedOperationException("Method not implemented yet");
    }

}
