package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.Addressbook;
import me.bmordue.redweed.model.dto.IngestVCardResponseDto;
import me.bmordue.redweed.service.VCardImportService;

import java.util.List;

/**
 * Controller for handling vCard imports from CalDAV servers.
 */
@Controller("/api/vcard/import")
public class VCardImportController {

    private final VCardImportService vCardImportService;

    /**
     * Constructor.
     *
     * @param vCardImportService the vCard import service
     */
    @Inject
    public VCardImportController(VCardImportService vCardImportService) {
        this.vCardImportService = vCardImportService;
    }

    /**
     * Imports vCards from a CalDAV server.
     *
     * @param request the import request
     * @return the import response
     */
    @Post("/caldav")
    public IngestVCardResponseDto importFromCalDAV(@Body VCardImportRequest request) {
        return vCardImportService.importVCards(request);
    }

    /**
     * Imports a batch of vCard strings.
     *
     * @param vcards the list of vCard strings
     * @return the import response
     */
    @Post("/batch")
    public IngestVCardResponseDto importBatch(@Body List<String> vcards) {
        return vCardImportService.importVCardBatch(vcards);
    }

    /**
     * Lists available addressbooks from a CalDAV server.
     *
     * @param caldavUrl the CalDAV server URL
     * @param username  the username for authentication
     * @param password  the password for authentication
     * @return list of available addressbooks
     */
    @Get("/addressbooks")
    public List<Addressbook> listAddressbooks(
            @QueryValue String caldavUrl,
            @QueryValue String username,
            @QueryValue String password) {
        return vCardImportService.listAddressbooks(caldavUrl, username, password);
    }

    @Post("/vcard")
    public HttpResponse<?> importVCards(@Body VCardImportRequest request) {
        if (request.addressbookUrl() == null) {
            return HttpResponse.badRequest("addressbookUrl must not be null or empty");
        }
        if (request.username() == null || request.username().isEmpty()) {
            return HttpResponse.badRequest("username must not be null or empty");
        }
        if (request.password() == null || request.password().isEmpty()) {
            return HttpResponse.badRequest("password must not be null or empty");
        }
        vCardImportService.importVCards(request.addressbookUrl(), request.username(), request.password());
        return HttpResponse.ok();
    }
}
