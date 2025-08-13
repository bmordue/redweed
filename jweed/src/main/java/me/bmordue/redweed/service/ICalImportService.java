package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.exception.ICalParsingException;
import me.bmordue.redweed.model.dto.IngestICalResponseDto;
import me.bmordue.redweed.repository.EventRepository;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for importing iCal data.
 */
@Singleton
public class ICalImportService {

    private static final Logger log = LoggerFactory.getLogger(ICalImportService.class);

    private final EventRepository eventRepository;

    /**
     * Constructor.
     *
     * @param eventRepository the event repository
     */
    @Inject
    public ICalImportService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Imports a single iCal string.
     *
     * @param ics the iCal string
     * @return the response containing import results
     */
    public IngestICalResponseDto importICalendar(String ics) {
        log.info("Importing iCal data");
        
        if (ics == null || ics.trim().isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "iCal data cannot be null or empty");
        }
        
        try {
            Model model = ICalToRdfConverter.convert(ics);
            eventRepository.save(model);
            
            String message = "iCal data imported successfully";
            log.info(message);
            
            return new IngestICalResponseDto(message, 1);
            
        } catch (ICalParsingException e) {
            log.error("Failed to parse iCal data", e);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, 
                    "Failed to parse iCal data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to import iCal data", e);
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to import iCal data: " + e.getMessage());
        }
    }

    /**
     * Imports a batch of iCal strings.
     *
     * @param icsList the list of iCal strings to import
     * @return the response containing import results
     */
    public IngestICalResponseDto importICalendarBatch(List<String> icsList) {
        log.info("Importing batch of {} iCal entries", icsList.size());
        
        if (icsList.isEmpty()) {
            return new IngestICalResponseDto("No iCal data provided", 0);
        }
        
        int successCount = 0;
        int errorCount = 0;
        
        for (String ics : icsList) {
            try {
                importSingleICal(ics);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to import iCal in batch", e);
                errorCount++;
            }
        }
        
        String message = String.format("Batch import completed: %d successful, %d errors", 
                successCount, errorCount);
        log.info(message);
        
        return new IngestICalResponseDto(message, successCount);
    }

    /**
     * Imports a single iCal string without error handling.
     *
     * @param ics the iCal string
     */
    private void importSingleICal(String ics) {
        Model model = ICalToRdfConverter.convert(ics);
        eventRepository.save(model);
    }
}