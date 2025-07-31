package me.bmordue.redweed.service;

import jakarta.inject.Singleton;
import me.bmordue.redweed.repository.RdfRepository;
import org.apache.jena.rdf.model.Model;

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
        List<String> vcardStrings = caldavService.getVCards(addressbookUrl, username, password);
        for (String vcardString : vcardStrings) {
            try {
                Model model = VCardToRdfConverter.convert(vcardString);
                rdfRepository.save(model);
            } catch (Exception e) {
                System.err.println("Failed to convert vCard: " + vcardString);
                e.printStackTrace();
            }
        }
    }
}
