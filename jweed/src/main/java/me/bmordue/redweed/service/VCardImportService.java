package me.bmordue.redweed.service;

import jakarta.inject.Singleton;
import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.net.URI;
import java.util.List;

@Singleton
public class VCardImportService {

    private final CaldavService caldavService;
    private final RdfRepository rdfRepository;

    public VCardImportService(CaldavService caldavService, RdfRepository rdfRepository) {
        this.caldavService = caldavService;
        this.rdfRepository = rdfRepository;
    }

    public void importVCards(URI addressbookUrl, String username, String password) {
        List<String> empty = List.of();
        List<String> vcardStrings = caldavService.getVCards(addressbookUrl, username, password, empty);
        Model combinedModel = ModelFactory.createDefaultModel();
        for (String vcardString : vcardStrings) {
            combinedModel.add(VCardToRdfConverter.convert(vcardString));
        }
        rdfRepository.save(combinedModel);
    }
}
