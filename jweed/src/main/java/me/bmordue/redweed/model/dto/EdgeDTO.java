package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

/**
 * A data transfer object for an edge in a graph.
 *
 * @param from  the source node
 * @param to    the target node
 * @param label the edge label
 */
@Serdeable
public record EdgeDTO(String from, String to, String label) {
}
