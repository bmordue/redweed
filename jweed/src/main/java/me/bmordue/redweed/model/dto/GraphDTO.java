package me.bmordue.redweed.model.dto;

import java.util.List;

public class GraphDTO {
    private final List<NodeDTO> nodes;
    private final List<EdgeDTO> edges;

    public GraphDTO(List<NodeDTO> nodes, List<EdgeDTO> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<NodeDTO> getNodes() {
        return nodes;
    }

    public List<EdgeDTO> getEdges() {
        return edges;
    }
}
