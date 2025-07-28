package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestMp4ResponseDto;
import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp4Parser;
import me.bmordue.redweed.vocabulary.MediaVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Singleton
public class MediaService {

    private final RdfRepository rdfRepository;
    private final MediaVocabulary mediaVocabulary;

    @Inject
    public MediaService(RdfRepository rdfRepository, MediaVocabulary mediaVocabulary) {
        this.rdfRepository = rdfRepository;
        this.mediaVocabulary = mediaVocabulary;
    }

    public IngestMp4ResponseDto ingestMp4(File file, String canonicalUri) {
        Map<String, Object> metadata = Mp4Parser.parse(file);
        Model model = ModelFactory.createDefaultModel();
        String resourceUri = mediaVocabulary.getResourceNamespace() + UUID.randomUUID();
        Resource resource = model.createResource(resourceUri)
            .addProperty(RDF.type, model.createResource(MediaVocabulary.MA_MEDIA_RESOURCE))
            .addProperty(model.createProperty(MediaVocabulary.MA_CANONICAL_LOCATION), canonicalUri);

        if (metadata.get("title") != null) {
            resource.addProperty(model.createProperty(MediaVocabulary.MA_TITLE), metadata.get("title").toString());
        }
        if (metadata.get("creationDate") != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime((java.util.Date) metadata.get("creationDate"));
            resource.addProperty(model.createProperty(MediaVocabulary.MA_CREATION_DATE), model.createTypedLiteral(cal));
        }

        rdfRepository.save(model);

        return new IngestMp4ResponseDto(resourceUri, "MP4 ingested successfully");
    }
}
