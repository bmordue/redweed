package me.bmordue.redweed.util;

public class SparqlQueryBuilder {

    public String buildSelectAll() {
        return "SELECT * WHERE { ?s ?p ?o . }";
    }

    public String buildSelectById(String uri) {
        return String.format("SELECT * WHERE { <%s> ?p ?o . }", uri);
    }
}
