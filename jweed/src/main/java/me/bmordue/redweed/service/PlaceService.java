package me.bmordue.redweed.service;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.repository.PlaceRepository;
import me.bmordue.redweed.util.KmlParser;
import me.bmordue.redweed.vocabulary.RedweedVocab;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class PlaceService {

    private static final Logger log = LoggerFactory.getLogger(PlaceService.class);

    @Inject
    private PlaceRepository placeRepository;

    @Inject
    private RedweedVocab redweedVocab;

    public IngestKmlResponseDto ingestKml(String kmlString) {
        try {
            List<Map<String, String>> placemarks = KmlParser.parse(kmlString);
            Model model = ModelFactory.createDefaultModel();
            List<String> placeUris = placemarks.stream()
                    .map(placemark -> {
                        String placeUri = redweedVocab.getPlaceNamespace() + UUID.randomUUID();
                        Resource placeResource = model.createResource(placeUri)
                                .addProperty(RDF.type, model.createResource(RedweedVocab.GEO_SPATIAL_THING))
                                .addProperty(RDFS.label, placemark.get("name"))
                                .addProperty(model.createProperty(RedweedVocab.GEO_LAT), placemark.get("latitude"))
                                .addProperty(model.createProperty(RedweedVocab.GEO_LONG), placemark.get("longitude"));
                        if (placemark.get("description") != null) {
                            placeResource.addProperty(RDFS.comment, placemark.get("description"));
                        }
                        return placeUri;
                    })
                    .collect(Collectors.toList());

            placeRepository.save(model);

            return new IngestKmlResponseDto(placeUris, "KML ingested successfully");
        } catch (IllegalArgumentException e) {
            log.error("Invalid KML", e);
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid KML");
        }
    }
}
