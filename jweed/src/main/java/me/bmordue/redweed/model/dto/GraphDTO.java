package me.bmordue.redweed.model.dto;

import java.util.Collection;

public record GraphDTO(Collection<NodeDTO> nodes, Collection<EdgeDTO> edges) {
}
