package me.bmordue.redweed.model.dto;

import java.util.Objects;

public class NodeDTO {
    private final String id;
    private final String label;

    public NodeDTO(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

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
