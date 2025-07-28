package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Objects;

/**
 * A data transfer object for a node in a graph.
 *
 * @param id    the node ID
 * @param label the node label
 */
@Serdeable
public record NodeDTO(String id, String label) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDTO nodeDTO = (NodeDTO) o;
        return Objects.equals(id, nodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
