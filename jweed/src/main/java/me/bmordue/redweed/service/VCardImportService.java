package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.controller.VCardImportRequest;
import me.bmordue.redweed.model.Addressbook;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.repository.PersonRepository;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for importing vCards from CalDAV servers.
 */
@Singleton
public class VCardImportService {

    private static final Logger log = LoggerFactory.getLogger(VCardImportService.class);

    private final CaldavService caldavService;
    private final PersonRepository personRepository;

    /**
     * Constructor.
     *
     * @param caldavService    the CalDAV service
     * @param personRepository the person repository
     */
    @Inject
    public VCardImportService(CaldavService caldavService, PersonRepository personRepository) {
        this.caldavService = caldavService;
        this.personRepository = personRepository;
    }

    /**
     * Imports vCards from a CalDAV server.
     *
     * @param request the import request
     * @return the response containing import results
     */
    public IngestVCardResponseDto importVCards(VCardImportRequest request) {
        log.info("Starting vCard import from CalDAV server: {}", request.getCaldavUrl());
        
        try {
            // Discover addressbooks
            List<Addressbook> addressbooks = caldavService.discoverAddressbooks(
                    request.getCaldavUrl(), 
                    request.getUsername(), 
                    request.getPassword()
            );
            
            // Find the requested addressbook
            Addressbook targetAddressbook = findAddressbook(addressbooks, request.getAddressbookName());
            if (targetAddressbook == null) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, 
                        "Addressbook not found: " + request.getAddressbookName());
            }
            
            // Fetch vCards from the addressbook
            List<String> vcards = caldavService.fetchVCards(
                    targetAddressbook, 
                    request.getUsername(), 
                    request.getPassword()
            );
            
            if (vcards.isEmpty()) {
                log.warn("No vCards found in addressbook: {}", request.getAddressbookName());
                return new IngestVCardResponseDto("No vCards found in addressbook");
            }
            
            // Import each vCard
            int successCount = 0;
            int errorCount = 0;
            
            for (String vcard : vcards) {
                try {
                    importSingleVCard(vcard);
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to import vCard", e);
                    errorCount++;
                }
            }
            
            String message = String.format("Import completed: %d successful, %d errors", 
                    successCount, errorCount);
            log.info(message);
            
            return new IngestVCardResponseDto(message);
            
        } catch (HttpStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to import vCards from CalDAV", e);
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to import vCards: " + e.getMessage());
        }
    }

    /**
     * Imports a batch of vCard strings.
     *
     * @param vcards the list of vCard strings to import
     * @return the response containing import results
     */
    public IngestVCardResponseDto importVCardBatch(List<String> vcards) {
        log.info("Importing batch of {} vCards", vcards.size());
        
        if (vcards.isEmpty()) {
            return new IngestVCardResponseDto("No vCards provided");
        }
        
        int successCount = 0;
        int errorCount = 0;
        
        for (String vcard : vcards) {
            try {
                importSingleVCard(vcard);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to import vCard in batch", e);
                errorCount++;
            }
        }
        
        String message = String.format("Batch import completed: %d successful, %d errors", 
                successCount, errorCount);
        log.info(message);
        
        return new IngestVCardResponseDto(message);
    }

    /**
     * Imports a single vCard string.
     *
     * @param vcard the vCard string
     */
    private void importSingleVCard(String vcard) {
        Model model = VCardToRdfConverter.convert(vcard);
        personRepository.save(model);
    }

    /**
     * Finds an addressbook by name from a list of addressbooks.
     *
     * @param addressbooks the list of addressbooks
     * @param name         the name to search for
     * @return the found addressbook or null if not found
     */
    private Addressbook findAddressbook(List<Addressbook> addressbooks, String name) {
        return addressbooks.stream()
                .filter(ab -> name.equals(ab.getName()) || name.equals(ab.getDisplayName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lists available addressbooks from a CalDAV server.
     *
     * @param caldavUrl the CalDAV server URL
     * @param username  the username for authentication
     * @param password  the password for authentication
     * @return list of available addressbooks
     */
    public List<Addressbook> listAddressbooks(String caldavUrl, String username, String password) {
        log.info("Listing addressbooks from CalDAV server: {}", caldavUrl);
        
        try {
            return caldavService.discoverAddressbooks(caldavUrl, username, password);
        } catch (Exception e) {
            log.error("Failed to list addressbooks", e);
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to list addressbooks: " + e.getMessage());
        }
    }
}