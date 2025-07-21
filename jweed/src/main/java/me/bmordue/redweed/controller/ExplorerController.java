package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.EdgeDTO;
import me.bmordue.redweed.model.dto.GraphDTO;
import me.bmordue.redweed.model.dto.NodeDTO;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;

import java.util.HashSet;

@Controller("/api/graph")
public class ExplorerController {

    @Inject
    private Dataset dataset;

    @Get
    public GraphDTO getGraph() {
        HashSet<NodeDTO> nodes = new HashSet<>();
        HashSet<EdgeDTO> edges = new HashSet<>();

        dataset.begin(ReadWrite.READ);
        try {
            try (QueryExecution qexec = QueryExecutionFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }", dataset)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    RDFNode s = solution.get("s");
                    RDFNode p = solution.get("p");
                    RDFNode o = solution.get("o");

                    NodeDTO sourceNode = new NodeDTO(s.toString(), s.toString());
                    nodes.add(sourceNode);

                    NodeDTO targetNode = new NodeDTO(o.toString(), o.toString());
                    nodes.add(targetNode);

                    edges.add(new EdgeDTO(s.toString(), o.toString(), p.toString()));
                }
            }
        } finally {
            dataset.end();
        }

        return new GraphDTO(nodes, edges);
    }
}
