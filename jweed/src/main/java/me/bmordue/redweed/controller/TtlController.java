package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestTtlResponseDto;
import me.bmordue.redweed.service.TtlService;

@Controller("/ttl")
public class TtlController {

    private final TtlService ttlService;

    @Inject
    public TtlController(TtlService ttlService) {
        this.ttlService = ttlService;
    }

    @Post
    public IngestTtlResponseDto ingestTtl(@Body String body) {
        return ttlService.ingestTtl(body);
    }
}
