package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestKmlResponseDto;
import me.bmordue.redweed.repository.PlaceRepository;
import me.bmordue.redweed.util.KmlParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class PlaceService {

    @Inject
    private PlaceRepository placeRepository;

    public IngestKmlResponseDto ingestKml(String kmlString) {
        List<Map<String, String>> placemarks = KmlParser.parse(kmlString);
        Model model = ModelFactory.createDefaultModel();
        List<String> placeUris = placemarks.stream()
                .map(placemark -> {
                    String placeUri = "http://redweed.local/place/" + UUID.randomUUID();
                    Resource placeResource = model.createResource(placeUri)
                            .addProperty(RDF.type, model.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing"))
                            .addProperty(RDFS.label, placemark.get("name"))
                            .addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat"), placemark.get("latitude"))
                            .addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long"), placemark.get("longitude"));
                    if (placemark.get("description") != null) {
                        placeResource.addProperty(RDFS.comment, placemark.get("description"));
                    }
                    return placeUri;
                })
                .collect(Collectors.toList());

        placeRepository.save(model);

        return new IngestKmlResponseDto(placeUris, "KML ingested successfully");
    }
}
