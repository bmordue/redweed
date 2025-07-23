package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestMp3ResponseDto;
import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.Mp3Parser;
import me.bmordue.redweed.vocabulary.MusicVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Singleton
public class MusicService {

    private final RdfRepository rdfRepository;
    private final MusicVocabulary musicVocabulary;

    @Inject
    public MusicService(RdfRepository rdfRepository, MusicVocabulary musicVocabulary) {
        this.rdfRepository = rdfRepository;
        this.musicVocabulary = musicVocabulary;
    }

    public IngestMp3ResponseDto ingestMp3(File file) {
        Map<String, String> metadata = Mp3Parser.parse(file);
        Model model = ModelFactory.createDefaultModel();
        String workUri = musicVocabulary.getWorkNamespace() + UUID.randomUUID();
        Resource workResource = model.createResource(workUri)
                .addProperty(RDF.type, model.createResource(MusicVocabulary.MO_MUSICAL_WORK));

        addPropertyIfPresent(model, workResource, MusicVocabulary.MO_TITLE, metadata.get("title"));
        addPropertyIfPresent(model, workResource, MusicVocabulary.MO_ARTIST, metadata.get("artist"));
        addPropertyIfPresent(model, workResource, MusicVocabulary.MO_ALBUM, metadata.get("album"));
        addPropertyIfPresent(model, workResource, MusicVocabulary.MO_TRACK_NUMBER, metadata.get("track"));
        addPropertyIfPresent(model, workResource, MusicVocabulary.MO_GENRE, metadata.get("genre"));

        rdfRepository.save(model);

        return new IngestMp3ResponseDto(workUri, "MP3 ingested successfully");
    }

    private void addPropertyIfPresent(Model model, Resource resource, String propertyUri, String value) {
        if (value != null && !value.isEmpty()) {
            resource.addProperty(model.createProperty(propertyUri), value);
        }
    }
}
