package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Collection;

/**
 * A data transfer object for a graph.
 *
 * @param nodes the nodes in the graph
 * @param edges the edges in the graph
 */
@Serdeable
public record GraphDTO(Collection<NodeDTO> nodes, Collection<EdgeDTO> edges) {
}
