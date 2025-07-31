package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.CaldavImportResponseDto;
import me.bmordue.redweed.service.CaldavService;
import me.bmordue.redweed.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller("/caldav")
public class CaldavController {

    private static final Logger log = LoggerFactory.getLogger(CaldavController.class);

    private final CaldavService caldavService;
    private final PersonService personService;

    @Inject
    public CaldavController(CaldavService caldavService, PersonService personService) {
        this.caldavService = caldavService;
        this.personService = personService;
    }

    @Post("/import")
    public HttpResponse<CaldavImportResponseDto> importFromCaldav() {
        log.info("Starting CalDAV import.");
        List<String> vcards = caldavService.fetchVCardResources();
        int importCount = 0;
        for (String vcard : vcards) {
            try {
                personService.ingestVCard(vcard);
                importCount++;
            } catch (Exception e) {
                log.error("Failed to import a vCard, skipping.", e);
            }
        }
        log.info("Finished CalDAV import. Imported {} vCards.", importCount);
        CaldavImportResponseDto response = new CaldavImportResponseDto("Import complete", importCount);
        return HttpResponse.ok(response);
    }
}
