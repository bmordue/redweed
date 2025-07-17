package me.bmordue.redweed.repository;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ReviewRepository extends RdfRepository {

    private static final Logger log = LoggerFactory.getLogger(ReviewRepository.class);

}
