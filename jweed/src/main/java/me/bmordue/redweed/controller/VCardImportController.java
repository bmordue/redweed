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
        vCardImportService.importVCards(request.addressbookUrl(), request.username(), request.password());
        return HttpResponse.ok();
    }
}
