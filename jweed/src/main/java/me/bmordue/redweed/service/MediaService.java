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

    @Inject
    private RdfRepository rdfRepository;

    @Inject
    private MediaVocabulary mediaVocabulary;

    public IngestMp4ResponseDto ingestMp4(File file) {
        Map<String, Object> metadata = Mp4Parser.parse(file);
        Model model = ModelFactory.createDefaultModel();
        String resourceUri = mediaVocabulary.getResourceNamespace() + UUID.randomUUID();
        Resource resource = model.createResource(resourceUri)
                .addProperty(RDF.type, model.createResource(MediaVocabulary.MA_MEDIA_RESOURCE));

        if (metadata.get("title") != null) {
            resource.addProperty(model.createProperty(MediaVocabulary.MA_TITLE), metadata.get("title").toString());
        }
        if (metadata.get("creationDate") != null) {
            resource.addProperty(model.createProperty(MediaVocabulary.MA_CREATION_DATE), metadata.get("creationDate").toString());
        }

        rdfRepository.save(model);

        return new IngestMp4ResponseDto(resourceUri, "MP4 ingested successfully");
    }
}
