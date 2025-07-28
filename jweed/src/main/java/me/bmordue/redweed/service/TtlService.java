package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestTtlResponseDto;
import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;

/**
 * Service for TTL files.
 */
@Singleton
public class TtlService {

    private static final Logger log = LoggerFactory.getLogger(TtlService.class);

    private final RdfRepository rdfRepository;

    /**
     * Constructor.
     *
     * @param rdfRepository the RDF repository
     */
    @Inject
    public TtlService(RdfRepository rdfRepository) {
        this.rdfRepository = rdfRepository;
    }

    /**
     * Ingest a TTL file.
     *
     * @param ttl the TTL file content
     * @return the response
     */
    public IngestTtlResponseDto ingestTtl(String ttl) {
        Model model = ModelFactory.createDefaultModel();
        try {
            RDFParser.create()
                    .source(new StringReader(ttl))
                    .lang(Lang.TTL)
                    .parse(model);
        } catch (RiotException e) {
            log.error("Invalid TTL data", e);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid TTL");
        }

        rdfRepository.save(model);
        return new IngestTtlResponseDto("Success");
    }
}
