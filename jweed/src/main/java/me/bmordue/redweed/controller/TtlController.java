package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.IngestTtlResponseDto;
import me.bmordue.redweed.service.TtlService;

/**
 * Controller for handling TTL files.
 */
@Controller("/ttl")
public class TtlController {

    private final TtlService ttlService;

    /**
     * Constructor.
     *
     * @param ttlService the TTL service
     */
    @Inject
    public TtlController(TtlService ttlService) {
        this.ttlService = ttlService;
    }

    /**
     * Ingest a TTL file.
     *
     * @param body the TTL file body
     * @return the response
     */
    @Post
    public IngestTtlResponseDto ingestTtl(@Body String body) {
        return ttlService.ingestTtl(body);
    }
}
