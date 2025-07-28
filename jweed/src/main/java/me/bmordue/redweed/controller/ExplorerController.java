package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.EdgeDTO;
import me.bmordue.redweed.model.dto.GraphDTO;
import me.bmordue.redweed.model.dto.NodeDTO;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Controller for exploring the graph.
 */
@Controller("/api/graph")
public class ExplorerController {

    private final Dataset dataset;

    /**
     * Constructor.
     *
     * @param dataset the dataset
     */
    @Inject
    public ExplorerController(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Get the graph.
     *
     * @param limit the limit
     * @param offset the offset
     * @return the graph
     */
    @Get
    public GraphDTO getGraph(@io.micronaut.http.annotation.QueryValue(defaultValue = "100") int limit, @io.micronaut.http.annotation.QueryValue(defaultValue = "0") int offset) {
        java.util.Map<String, NodeDTO> nodes = new java.util.HashMap<>();
        java.util.Set<EdgeDTO> edges = new java.util.HashSet<>();

        dataset.begin(ReadWrite.READ);
        try {
            // Programmatically build the query to safely add LIMIT and OFFSET
            Query query = org.apache.jena.query.QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }");
            query.setLimit(limit);
            query.setOffset(offset);

            try (QueryExecution qexec = org.apache.jena.query.QueryExecutionFactory.create(query, dataset)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    RDFNode s = solution.get("s");
                    RDFNode p = solution.get("p");
                    RDFNode o = solution.get("o");

                    String sourceId = s.toString();
                    // Use a map to ensure nodes are created only once
                    nodes.computeIfAbsent(sourceId, id -> new NodeDTO(id, id));

                    String targetId = o.toString();
                    nodes.computeIfAbsent(targetId, id -> new NodeDTO(id, id));

                    edges.add(new EdgeDTO(sourceId, targetId, p.toString()));
                }
            }
        } finally {
            dataset.end();
        }

        return new GraphDTO(nodes.values(), edges);
    }
}
