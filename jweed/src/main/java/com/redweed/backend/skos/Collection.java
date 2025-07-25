/*
 * (c) Copyright 2022 Redweed, Inc. All rights reserved.
 */

package com.redweed.backend.skos;

import com.redweed.backend.vocab.Redweed;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class Collection extends ResourceImpl {
private Collection(final Resource s, final Model m) {
    super(s.getURI(), m);
}

    public static Collection fromResource(final Resource s) {
        return new Collection(s, s.getModel());
    }

    public void addMember(final Resource resource) {
        addProperty(SKOS.member, resource);
    }

    public void addName(final String name) {
        addProperty(Redweed.name, name);
    }
}
