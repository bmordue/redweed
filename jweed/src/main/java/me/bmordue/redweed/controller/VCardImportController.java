package me.bmordue.redweed.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import me.bmordue.redweed.service.VCardImportService;

@Controller("/import")
public class VCardImportController {

    private final VCardImportService vCardImportService;

    public VCardImportController(VCardImportService vCardImportService) {
        this.vCardImportService = vCardImportService;
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
