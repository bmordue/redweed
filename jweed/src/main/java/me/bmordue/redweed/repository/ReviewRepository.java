package me.bmordue.redweed.repository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ReviewRepository {

    private static final Logger log = LoggerFactory.getLogger(ReviewRepository.class);

    @Inject
    private RdfRepository rdfRepository;

    public void save(Model model) {
        rdfRepository.save(model);
    }
}
