package me.bmordue.redweed.model.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Collection;

@Serdeable
public record GraphDTO(Collection<NodeDTO> nodes, Collection<EdgeDTO> edges) {
}
