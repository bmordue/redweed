package me.bmordue.redweed.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.bmordue.redweed.model.dto.IngestEpubResponseDto;
import me.bmordue.redweed.repository.RdfRepository;
import me.bmordue.redweed.util.EpubParser;
import me.bmordue.redweed.vocabulary.BookVocabulary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Singleton
public class BookService {

    private final RdfRepository rdfRepository;
    private final BookVocabulary bookVocabulary;

    @Inject
    public BookService(RdfRepository rdfRepository, BookVocabulary bookVocabulary) {
        this.rdfRepository = rdfRepository;
        this.bookVocabulary = bookVocabulary;
    }

    public IngestEpubResponseDto ingestEpub(File file) {
        Map<String, String> metadata = EpubParser.parse(file);
        Model model = ModelFactory.createDefaultModel();
        String bookUri = bookVocabulary.getBookNamespace() + UUID.randomUUID();
        Resource bookResource = model.createResource(bookUri)
                .addProperty(RDF.type, model.createResource(BookVocabulary.BIBO_BOOK));

        addPropertyIfPresent(model, bookResource, BookVocabulary.DC_TITLE, metadata.get("title"));
        addPropertyIfPresent(model, bookResource, BookVocabulary.DC_CREATOR, metadata.get("creator"));
        addPropertyIfPresent(model, bookResource, BookVocabulary.DC_PUBLISHER, metadata.get("publisher"));
        addPropertyIfPresent(model, bookResource, BookVocabulary.DC_DATE, metadata.get("date"));

        rdfRepository.save(model);

        return new IngestEpubResponseDto(bookUri, "EPUB ingested successfully");
    }

    private void addPropertyIfPresent(Model model, Resource resource, String propertyUri, String value) {
        if (value != null && !value.isEmpty()) {
            resource.addProperty(model.createProperty(propertyUri), value);
        }
    }
}
