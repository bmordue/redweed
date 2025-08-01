package me.bmordue.redweed.model.domain;

import me.bmordue.redweed.vocabulary.SKOS;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public final class Collection extends ResourceImpl {
    Collection(final Resource s, final Model m) {
        super(s.asNode(), (ModelCom) m);
    }

    public static Collection fromResource(final Resource s) {
        if (s == null) {
            throw new IllegalArgumentException("Resource parameter cannot be null");
        }
        return new Collection(s, s.getModel());
    }

    public void addMember(final Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource parameter cannot be null");
        }
        addProperty(SKOS.MEMBER, resource);
    }
}
