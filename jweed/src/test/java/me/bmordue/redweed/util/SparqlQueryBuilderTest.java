package me.bmordue.redweed.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SparqlQueryBuilderTest {

    @Test
    void testBuildSelectAll() {
        String expected = "SELECT * WHERE { ?s ?p ?o . }";
        String actual = new SparqlQueryBuilder().buildSelectAll();
        assertEquals(expected, actual);
    }

    @Test
    void testBuildSelectById() {
        String expected = "SELECT * WHERE { <http://example.com/1> ?p ?o . }";
        String actual = new SparqlQueryBuilder().buildSelectById("http://example.com/1");
        assertEquals(expected, actual);
    }
}
