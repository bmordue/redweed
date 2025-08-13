package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestICalResponseDto;
import me.bmordue.redweed.service.ICalImportService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller for handling iCal imports.
 */
@Controller("/api/ical/import")
public class ICalImportController {

    private final ICalImportService iCalImportService;

    /**
     * Constructor.
     *
     * @param iCalImportService the iCal import service
     */
    @Inject
    public ICalImportController(ICalImportService iCalImportService) {
        this.iCalImportService = iCalImportService;
    }

    /**
     * Imports iCal data from a string.
     *
     * @param ics the iCal data
     * @return the import response
     */
    @Post(consumes = {MediaType.TEXT_PLAIN, "text/calendar"})
    public IngestICalResponseDto importICalendar(@Body String ics) {
        return iCalImportService.importICalendar(ics);
    }

    /**
     * Imports a batch of iCal strings.
     *
     * @param icsList the list of iCal strings
     * @return the import response
     */
    @Post("/batch")
    public IngestICalResponseDto importBatch(@Body List<String> icsList) {
        return iCalImportService.importICalendarBatch(icsList);
    }

    /**
     * Imports iCal data from an uploaded file.
     *
     * @param file the uploaded iCal file
     * @return the import response
     */
    @Post(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<IngestICalResponseDto> importFile(@Part("file") CompletedFileUpload file) {
        try {
            String ics = new String(file.getBytes(), StandardCharsets.UTF_8);
            IngestICalResponseDto response = iCalImportService.importICalendar(ics);
            
            return HttpResponse.created(response);
            
        } catch (IOException e) {
            return HttpResponse.serverError();
        }
    }
}