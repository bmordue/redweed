package me.bmordue.redweed.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import me.bmordue.redweed.model.dto.GraphDTO;
import me.bmordue.redweed.model.dto.NodeDTO;
import me.bmordue.redweed.model.dto.EdgeDTO;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

@Controller("/api/graph")
public class ExplorerController {

    @Inject
    private Dataset dataset;

    @Get
    public GraphDTO getGraph() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        int idCounter = 0;

        try (QueryExecution qexec = QueryExecutionFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o }", dataset)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                RDFNode s = solution.get("s");
                RDFNode p = solution.get("p");
                RDFNode o = solution.get("o");

                NodeDTO sourceNode = new NodeDTO(String.valueOf(s.hashCode()), s.toString());
                if (!nodes.contains(sourceNode)) {
                    nodes.add(sourceNode);
                }

                NodeDTO targetNode = new NodeDTO(String.valueOf(o.hashCode()), o.toString());
                if (!nodes.contains(targetNode)) {
                    nodes.add(targetNode);
                }

                edges.add(new EdgeDTO(String.valueOf(s.hashCode()), String.valueOf(o.hashCode()), p.toString()));
            }
        }

        return new GraphDTO(nodes, edges);
    }
}
